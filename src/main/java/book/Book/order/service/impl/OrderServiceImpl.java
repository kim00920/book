package book.Book.order.service.impl;

import book.Book.book.domain.Book;
import book.Book.error.BusinessException;
import book.Book.mapper.*;
import book.Book.order.domain.Order;
import book.Book.order.domain.OrderItem;
import book.Book.order.dto.OrderCreateRequest;
import book.Book.order.dto.OrderResponse;
import book.Book.order.service.OrderService;
import book.Book.user.domain.Cart;
import book.Book.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static book.Book.error.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final UserMapper userMapper;
    private final OrderMapper orderMapper;
    private final BookMapper bookMapper;
    private final CartMapper cartMapper;

    @Override
    public void createOrder(OrderCreateRequest orderCreateRequest) {
        log.info("주문 생성 요청 시작");

        User user = getUser();
        log.info("회원 조회 완료 - ID: {}, 로그인 ID: {}", user.getId(), user.getLoginId());

        int mileageToUse = orderCreateRequest.getMileage();
        int availableMileage = user.getMileage();
        log.info("사용 가능 마일리지: {}, 사용 요청 마일리지: {}", availableMileage, mileageToUse);

        if (mileageToUse > availableMileage) {
            throw new BusinessException(NOT_ENOUGH_MILEAGE);
        }

        List<OrderItem> orderItems = new ArrayList<>();

        for (OrderCreateRequest.Item itemRequest : orderCreateRequest.getItems()) {
            Long bookId = itemRequest.getBookId();
            int orderQuantity = itemRequest.getQuantity();

            Cart cart = cartMapper.findByUserIdAndBookId(user.getId(), bookId);
            if (cart == null) {
                throw new BusinessException(NOT_FOUND_CART);
            }

            log.info("장바구니 확인 - 도서 ID: {}, 장바구니 수량: {}, 주문 수량: {}", bookId, cart.getTotalAmount(), orderQuantity);

            if (orderQuantity > cart.getTotalAmount()) {
                throw new BusinessException(TO_MANY_CART_QUANTITY);
            }

            Book book = bookMapper.findById(bookId);
            if (book == null) {
                throw new BusinessException(NOT_FOUND_BOOK);
            }

            int orderPrice = book.getPrice() * orderQuantity;

            OrderItem orderItem = OrderItem.builder()
                    .bookId(book.getId())
                    .bookTitle(book.getTitle())
                    .price(book.getPrice())
                    .quantity(orderQuantity)
                    .orderPrice(orderPrice)
                    .userId(user.getId())
                    .build();

            orderItems.add(orderItem);

            if (orderQuantity == cart.getTotalAmount()) {
                cartMapper.deleteByUserIdAndBookId(user.getId(), bookId);
                log.info("장바구니 항목 삭제 - 도서 ID: {}", bookId);
            } else {
                cart.setTotalAmount(cart.getTotalAmount() - orderQuantity);
                cart.setTotalPrice(book.getPrice() * cart.getTotalAmount());
                cartMapper.updateCart(cart);
                log.info("장바구니 수량 갱신 - 도서 ID: {}, 남은 수량: {}", bookId, cart.getTotalAmount());
            }
        }

        int totalPrice = orderItems.stream()
                .mapToInt(OrderItem::getOrderPrice)
                .sum();

        int finalMileageToUse = Math.min(mileageToUse, totalPrice);
        int finalPrice = totalPrice - finalMileageToUse;

        log.info("총 주문 금액: {}, 사용 마일리지: {}, 최종 결제 금액: {}", totalPrice, finalMileageToUse, finalPrice);

        Order order = Order.builder()
                .userId(user.getId())
                .name(user.getUsername())
                .zipcode(user.getZipcode())
                .address(user.getAddress())
                .requirement("배송 시 문 앞에 놔주세요.")
                .totalPrice(finalPrice)
                .usedMileage(finalMileageToUse)
                .originalPrice(totalPrice)
                .orderStatus("PENDING")
                .impUid("imp_727855699150")
                .merchantId("ORD20180131-0000014")
                .createdAt(LocalDate.now())
                .build();

        orderMapper.insertOrder(order);
        log.info("주문 저장 완료 - 주문 ID: {}", order.getId());

        Long orderId = order.getId();
        if (orderId == null) {
            throw new BusinessException(NOT_FOUND_ORDER);
        }

        orderItems.forEach(item -> item.setOrderId(orderId));
        orderMapper.insertOrderItems(orderId, orderItems);
        log.info("주문 아이템 저장 완료 - 아이템 수: {}", orderItems.size());

        user.setMileage(user.getMileage() - finalMileageToUse);
        userMapper.updateUser(user);
        log.info("마일리지 차감 완료 - 남은 마일리지: {}", user.getMileage());
    }

    // 주문 전체 조회
    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> orderFindUser() {
        log.info("주문 전체 조회 요청");

        List<Order> orders = orderMapper.findAllOrders();
        if (orders.isEmpty()) {
            log.info("조회된 주문 없음");
            return List.of();
        }

        // 모든 주문 id만 뽑아서 가져옴
        List<Long> orderIds = orders.stream()
                .map(Order::getId)
                .toList();

        // 주문 id로 주문아이템 가져옴
        List<OrderItem> allOrderItems = orderMapper.findOrderItemsByOrderIds(orderIds);

        // 주문 id : 주문 아이템 으로 가져옴
        Map<Long, List<OrderItem>> itemsByOrderId = allOrderItems.stream()
                .filter(item -> item.getOrderId() != null)
                .collect(Collectors.groupingBy(OrderItem::getOrderId));

        log.info("주문 수: {}, 주문 아이템 수: {}", orders.size(), allOrderItems.size());

        return orders.stream()
                .map(order -> OrderResponse.toDto(order, itemsByOrderId.getOrDefault(order.getId(), List.of())))
                .toList();
    }

    // 주문 단건 조회
    @Override
    @Transactional(readOnly = true)
    public OrderResponse orderFindOne(Long orderId) {
        log.info("단일 주문 조회 요청 - 주문 ID: {}", orderId);

        Order order = orderMapper.findOrderById(orderId);
        if (order == null) {
            throw new BusinessException(NOT_FOUND_ORDER);
        }

        List<OrderItem> orderItems = orderMapper.findOrderItemsByOrderId(orderId);
        log.info("주문 조회 완료 - 아이템 수: {}", orderItems.size());

        return OrderResponse.toDto(order, orderItems);
    }

    // 현재 로그인한 회원의 주문 조회
    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> orderUserMyInfo() {
        log.info("로그인한 회원의 주문 목록 조회 요청");

        User user = getUser(); // 로그인한 유저 정보 획득
        List<Order> orders = orderMapper.findOrdersByUserId(user.getId());

        if (orders.isEmpty()) {
            log.info("조회된 주문 없음");
            return List.of();
        }

        List<Long> orderIds = orders.stream()
                .map(Order::getId)
                .toList();

        List<OrderItem> allOrderItems = orderMapper.findOrderItemsByOrderIds(orderIds);

        Map<Long, List<OrderItem>> itemsByOrderId = allOrderItems.stream()
                .filter(item -> item.getOrderId() != null)
                .collect(Collectors.groupingBy(OrderItem::getOrderId));

        log.info("주문 수: {}, 주문 아이템 수: {}", orders.size(), allOrderItems.size());

        return orders.stream()
                .map(order -> OrderResponse.toDto(order, itemsByOrderId.getOrDefault(order.getId(), List.of())))
                .toList();
    }

    // COMPLETE 일떄는 사용한 마일리지 환불, 사용자 누적 금액 차감
    // PENDING 일떄는 사용한 마일리지만 환불
    // CANCEL, FAIL 일때는 예외만
    @Override
    public void orderCancel(Long orderId) {
        User user = getUser();
        Order order = orderMapper.findOrderById(orderId);

        if (order == null) {
            throw new BusinessException(NOT_FOUND_ORDER);
        }

        if (!order.getUserId().equals(user.getId())) {
            throw new BusinessException(NOT_MATCH_USER);
        }

        String orderStatus = order.getOrderStatus();

        if ("CANCEL".equals(orderStatus)) {
            throw new BusinessException(ALREADY_CANCEL_ORDER);
        }

        if ("FAIL".equals(orderStatus)) {
            throw new BusinessException(ALREADY_FAIL_ORDER);
        }

        orderMapper.orderStatus("CANCEL", order.getId());
        log.info("주문 상태 변경 - 주문 ID: {}, 상태: CANCEL", orderId);

        int usedMil = order.getUsedMileage();
        user.setMileage(user.getMileage() + usedMil);
        log.info("마일리지 환불 - 환불 마일리지: {}, 누적 마일리지: {}", usedMil, user.getMileage());

        // 이미 성공한 결제일떄
        if ("COMPLETE".equals(orderStatus)) {
            int deductMil = (int) (order.getOriginalPrice() * 0.1);
            user.setMileage(user.getMileage() - deductMil);
            user.setTotalAmount(user.getTotalAmount() - order.getOriginalPrice());

            log.info("차감 마일리지: {}", deductMil);
        }

        userMapper.updateUser(user);
    }

    private User getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loginId = authentication.getName();

        User user = userMapper.findByLoginId(loginId);
        if (user == null) {
            throw new BusinessException(NOT_FOUND_USER);
        }

        log.info("로그인 사용자 확인 완료 - 로그인 ID: {}", loginId);
        return user;
    }
}

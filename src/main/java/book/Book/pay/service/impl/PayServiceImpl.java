package book.Book.pay.service.impl;

import book.Book.error.BusinessException;
import book.Book.mapper.OrderMapper;
import book.Book.mapper.PayMapper;
import book.Book.mapper.UserMapper;
import book.Book.order.domain.Order;
import book.Book.pay.domain.Pay;
import book.Book.pay.dto.PayCreateRequest;
import book.Book.pay.dto.PayResponse;
import book.Book.pay.service.PayService;
import book.Book.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;

import static book.Book.error.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PayServiceImpl implements PayService {

    private final OrderMapper orderMapper;
    private final PayMapper payMapper;
    private final UserMapper userMapper;

    @Override
    public void processPay(PayCreateRequest request) {
        log.info("결제 요청 시작 - 주문 ID: {}, 카드사: {}, 카드번호: {}",
                request.getOrderId(), request.getCardCompany(), request.getCardNumber());

        User user = getUser();
        log.info("회원 조회 완료 - 회원 ID: {}, 로그인 ID: {}", user.getId(), user.getLoginId());

        Order order = orderMapper.findOrderById(request.getOrderId());
        if (order == null) {
            log.warn("주문 없음 - 주문 ID: {}", request.getOrderId());
            throw new BusinessException(NOT_FOUND_ORDER);
        }
        log.info("주문 조회 완료 - 주문 ID: {}, 상태: {}", order.getId(), order.getOrderStatus());

        if (!order.getUserId().equals(user.getId())) {
            log.warn("주문자 불일치 - 요청자 ID: {}, 주문자 ID: {}", user.getId(), order.getUserId());
            throw new BusinessException(NOT_MATCH_USER);
        }

        int successfulPayments = payMapper.countCompletePay(request.getOrderId(), user.getId());
        if (successfulPayments > 0) {
            log.warn("중복 결제 시도 - 주문 ID: {}, 회원 ID: {}", request.getOrderId(), user.getId());
            throw new BusinessException(DUPLICATED_PAY);
        }

        boolean isPaymentSuccessful = new Random().nextInt(100) < 80;
        log.info("결제 시도 - 성공 여부: {}", isPaymentSuccessful ? "성공" : "실패");

        Pay pay = Pay.builder()
                .userId(order.getUserId())
                .order(order)
                .cardCompany(request.getCardCompany())
                .cardNumber(request.getCardNumber())
                .payPrice(order.getTotalPrice())
                .payStatus(isPaymentSuccessful ? "COMPLETE" : "FAIL")
                .createdAt(LocalDate.now())
                .build();

        payMapper.savePay(pay);
        log.info("결제 내역 저장 완료 - 상태: {}, 금액: {}", pay.getPayStatus(), pay.getPayPrice());

        if (isPaymentSuccessful) {
            orderMapper.orderStatus("COMPLETE", order.getId());
            log.info("주문 상태 변경 - 주문 ID: {}, 상태: COMPLETE", order.getId());

            sumMil(user, order.getOriginalPrice());
        } else {
            orderMapper.orderStatus("FAIL", order.getId());
            log.info("주문 상태 변경 - 주문 ID: {}, 상태: FAIL", order.getId());
        }
    }

    @Override
    public PayResponse findOnePay(Long id) {
        log.info("결제 조회 요청 - 결제 ID: {}", id);
        Pay pay = payMapper.findById(id);

        if (pay == null) {
            throw new BusinessException(NOT_FOUND_PAY);
        } else {
            log.info("결제 조회 성공 - 결제 ID: {}, 상태: {}", pay.getId(), pay.getPayStatus());
        }

        return PayResponse.toDto(pay);
    }


    @Override
    public List<PayResponse> findAllPay() {
        log.info("전체 결제 내역 조회 요청");
        List<Pay> payList = payMapper.findAll();
        log.info("전체 결제 내역 조회 완료 - 총 {}건", payList.size());

        return payList.stream()
                .map(PayResponse::toDto)
                .toList();
    }


    @Override
    public List<PayResponse> findAllUserPay() {
        User user = getUser();
        log.info("사용자 결제 내역 조회 요청 - 사용자 ID: {}", user.getId());

        List<Pay> payList = payMapper.findByUserId(user.getId());
        log.info("사용자 결제 내역 조회 완료 - 사용자 ID: {}, 총 {}건", user.getId(), payList.size());

        return payList.stream()
                .map(PayResponse::toDto)
                .toList();
    }

    // 결제 취소
    @Override
    public void cancelPay(Long id) {
        log.info("결제 취소 요청 - 결제 ID: {}", id);

        Pay pay = payMapper.findById(id);
        if (pay == null) {
            throw new BusinessException(NOT_FOUND_PAY);
        }

        if (!pay.getPayStatus().equals("COMPLETE")) {
            throw new BusinessException(NOT_COMPLETE_PAY);
        }

        User user = getUser();
        if (!pay.getUserId().equals(user.getId())) {
            throw new BusinessException(NOT_MATCH_USER);
        }

        // 주문 상태 변경
        Order order = pay.getOrder();
        orderMapper.orderStatus("CANCEL", order.getId());
        log.info("주문 상태 변경 - 주문 ID: {}, 상태: CANCEL", order.getId());

        // 결제 상태 변경
        pay.setPayStatus("CANCEL");
        payMapper.updatePay(pay); // update 메서드 필요
        log.info("결제 상태 변경 - 결제 ID: {}, 상태: CANCELED", id);

       rollbackMil(user, order.getOriginalPrice());
    }


    private User getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loginId = authentication.getName();

        User user = userMapper.findByLoginId(loginId);
        if (user == null) {
            log.warn("회원 조회 실패 - 로그인 ID: {}", loginId);
            throw new BusinessException(NOT_FOUND_USER);
        }

        log.info("로그인 사용자 확인 완료 - 로그인 ID: {}", loginId);
        return user;
    }

    // 회원 마일리지 랑 총 사용 금액 적립
    private void sumMil(User user, int originalPrice) {
        int mileageEarned = (int) (originalPrice * 0.1);
        user.setMileage(user.getMileage() + mileageEarned);
        user.setTotalAmount(user.getTotalAmount() + originalPrice);
        userMapper.updateUser(user);

        log.info("회원 정보 적립 - 마일리지: {}, 총 사용 금액: {}", mileageEarned, user.getTotalAmount());
    }

    // 회원 마일리지 랑 총 사용 금액 차감
    private void rollbackMil(User user, int originalPrice) {
        int mileageToSubtract = (int) (originalPrice * 0.1);
        user.setMileage(user.getMileage() - mileageToSubtract);
        user.setTotalAmount(user.getTotalAmount() - originalPrice);
        userMapper.updateUser(user);

        log.info("회원 정보 차감 - 마일리지: {}, 총 사용 금액: {}", mileageToSubtract, user.getTotalAmount());
    }
}

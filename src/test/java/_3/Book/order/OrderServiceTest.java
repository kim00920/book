package _3.Book.order;

import book.Book.book.domain.Book;
import book.Book.error.BusinessException;
import book.Book.mapper.OrderMapper;
import book.Book.order.domain.Order;
import book.Book.order.dto.OrderCreateRequest;
import book.Book.order.dto.OrderResponse;
import book.Book.order.service.impl.OrderServiceImpl;
import book.Book.user.domain.Cart;
import book.Book.user.domain.User;
import book.Book.mapper.BookMapper;
import book.Book.mapper.CartMapper;
import book.Book.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static _3.Book.factory.TestFactory.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("주문 단위테스트")
class OrderServiceTest {

    @Mock
    private UserMapper userMapper;
    @Mock
    private OrderMapper orderMapper;
    @Mock
    private BookMapper bookMapper;
    @Mock
    private CartMapper cartMapper;

    @InjectMocks
    private OrderServiceImpl orderService;

    private User user;
    private Book book;
    private Cart cart;


    @BeforeEach
    void setUp() {
        user = createUser();
        user.setMileage(2000);
        book = createBook();
        cart = createCart(user, book, 5);

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(user.getLoginId(), null, List.of());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);


    }

    @Test
    @DisplayName("주문 생성")
    void createOrderOK() {
        when(userMapper.findByLoginId(anyString())).thenReturn(user);

        when(userMapper.findByLoginId(anyString())).thenReturn(user);

        OrderCreateRequest orderCreateRequest = new OrderCreateRequest();
        orderCreateRequest.setMileage(1000);

        OrderCreateRequest.Item item = new OrderCreateRequest.Item();
        item.setBookId(book.getId());
        item.setQuantity(3);
        orderCreateRequest.setItems(List.of(item));

        when(cartMapper.findByUserIdAndBookId(user.getId(), book.getId())).thenReturn(cart);
        when(bookMapper.findById(book.getId())).thenReturn(book);

        doNothing().when(cartMapper).updateCart(any(Cart.class));

        // 주문 저장시 주문 객체에 id 세팅
        doAnswer(invocation -> {
            Order orderArg = invocation.getArgument(0);
            orderArg.setId(1L);  // 주문 ID를 임의로 부여
            return null;
        }).when(orderMapper).insertOrder(any(Order.class));

        doNothing().when(orderMapper).insertOrderItems(anyLong(), anyList());
        doNothing().when(userMapper).updateUser(any(User.class));

        orderService.createOrder(orderCreateRequest);

        verify(cartMapper, times(1)).updateCart(any(Cart.class));
        verify(orderMapper, times(1)).insertOrder(any());
        verify(orderMapper, times(1)).insertOrderItems(anyLong(), anyList());
        verify(userMapper, times(1)).updateUser(any(User.class));
    }



    @Test
    @DisplayName("주문 생성 실패 (마일리지 부족)")
    void createOrderFail_NotEnoughMileage() {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(user.getLoginId(), null, List.of());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        when(userMapper.findByLoginId(user.getLoginId())).thenReturn(user);

        OrderCreateRequest orderCreateRequest = new OrderCreateRequest();
        orderCreateRequest.setMileage(user.getMileage() + 1000); // 마일리지 초과
        OrderCreateRequest.Item item = new OrderCreateRequest.Item();
        item.setBookId(book.getId());
        item.setQuantity(1);
        orderCreateRequest.setItems(List.of(item));

        BusinessException ex = assertThrows(BusinessException.class, () -> {
            orderService.createOrder(orderCreateRequest);
        });

        assertEquals("NOT_ENOUGH_MILEAGE", ex.getErrorCode().name());
    }

    @Test
    @DisplayName("주문 생성 실패 (장바구니 수량보다 많음)")
    void orderCreateFail_ToManyCartQuantity() {
        when(userMapper.findByLoginId(anyString())).thenReturn(user);

        OrderCreateRequest orderCreateRequest = new OrderCreateRequest();
        orderCreateRequest.setMileage(0);
        OrderCreateRequest.Item item = new OrderCreateRequest.Item();
        item.setBookId(book.getId());
        item.setQuantity(cart.getTotalAmount() + 1); // 장바구니 수량 초과
        orderCreateRequest.setItems(List.of(item));

        when(cartMapper.findByUserIdAndBookId(user.getId(), book.getId())).thenReturn(cart);

        BusinessException ex = assertThrows(BusinessException.class, () -> {
            orderService.createOrder(orderCreateRequest);
        });

        assertEquals("TO_MANY_CART_QUANTITY", ex.getErrorCode().name());
    }

    @Test
    @DisplayName("주문 생성 실패 (존재하지 않는 책 선택)")
    void orderCreateFail_NotFoundBook() {
        when(userMapper.findByLoginId(anyString())).thenReturn(user);

        OrderCreateRequest orderCreateRequest = new OrderCreateRequest();
        orderCreateRequest.setMileage(0);
        OrderCreateRequest.Item item = new OrderCreateRequest.Item();
        item.setBookId(book.getId());
        item.setQuantity(1);
        orderCreateRequest.setItems(List.of(item));

        when(cartMapper.findByUserIdAndBookId(user.getId(), book.getId())).thenReturn(cart);
        when(bookMapper.findById(book.getId())).thenReturn(null);  // 책 없음

        BusinessException ex = assertThrows(BusinessException.class, () -> {
            orderService.createOrder(orderCreateRequest);
        });

        assertEquals("NOT_FOUND_BOOK", ex.getErrorCode().name());
    }

    @Test
    @DisplayName("주문 단건 조회")
    void findOrderByIdOK() {
        Order order = createOrder();
        when(orderMapper.findOrderById(order.getId())).thenReturn(order);

        OrderResponse result = orderService.orderFindOne(order.getId());

        assertNotNull(result);

        verify(orderMapper, times(1)).findOrderById(order.getId());
    }

    @Test
    @DisplayName("전체 주문 목록 조회")
    void findAllOrdersOK() {
        Order order1 = createOrder();
        Order order2 = createOrder();
        order2.setId(2L);

        List<Order> orders = List.of(order1, order2);
        when(orderMapper.findAllOrders()).thenReturn(orders);

        List<OrderResponse> result = orderService.orderFindUser();

        assertEquals(2, result.size());
        verify(orderMapper, times(1)).findAllOrders();
    }

    @Test
    @DisplayName("현재 로그인한 사용자의 주문 목록 조회 성공")
    void findOrdersByCurrentUserOK() {
        when(userMapper.findByLoginId(anyString())).thenReturn(user);

        Order order = createOrder();
        order.setUserId(user.getId());

        List<Order> userOrders = List.of(order);
        when(orderMapper.findOrdersByUserId(user.getId())).thenReturn(userOrders);

        List<OrderResponse> result = orderService.orderUserMyInfo();

        assertEquals(1, result.size());
        verify(orderMapper, times(1)).findOrdersByUserId(user.getId());
    }


    @Test
    @DisplayName("주문 취소")
    void cancelOrder() {
        Order order = createOrder();

        order.setUserId(user.getId());

        user.setMileage(0);
        user.setTotalAmount(100000);

        when(orderMapper.findOrderById(order.getId())).thenReturn(order);
        when(userMapper.findByLoginId(user.getLoginId())).thenReturn(user);

        int expectedMileage = user.getMileage() + order.getUsedMileage() - (int)(order.getOriginalPrice() * 0.1);
        int expectedTotalAmount = user.getTotalAmount() - order.getOriginalPrice();

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(user.getLoginId(), null, List.of());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        orderService.orderCancel(order.getId());

        assertEquals(expectedMileage, user.getMileage());
        assertEquals(expectedTotalAmount, user.getTotalAmount());

        verify(orderMapper).orderStatus("CANCEL", order.getId());
        verify(userMapper).updateUser(user);
    }

}

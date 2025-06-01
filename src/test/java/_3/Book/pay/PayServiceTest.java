package _3.Book.pay;

import book.Book.error.BusinessException;
import book.Book.mapper.OrderMapper;
import book.Book.mapper.PayMapper;
import book.Book.mapper.UserMapper;
import book.Book.order.domain.Order;
import book.Book.pay.domain.Pay;
import book.Book.pay.dto.PayCreateRequest;
import book.Book.pay.dto.PayResponse;
import book.Book.pay.service.impl.PayServiceImpl;
import book.Book.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static _3.Book.factory.TestFactory.*;
import static book.Book.error.ErrorCode.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PayServiceTest {

    @Mock
    private PayMapper payMapper;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private PayServiceImpl payService;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    private User user;
    private Order order;
    private Pay pay;

    @BeforeEach
    void setup() {
     user = createUser();
     order = createOrder();
     pay = createPay(user, order);


    }

    @Test
    @DisplayName("결제 단건 조회")
    void findOnePay() {
        when(payMapper.findById(100L)).thenReturn(pay);

        PayResponse response = payService.findOnePay(100L);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(100L);
        assertThat(response.getPayStatus()).isEqualTo("COMPLETE");
        verify(payMapper).findById(100L);
    }



    @Test
    @DisplayName("결제 전체 조회")
    void findAllPay() {
        List<Pay> payList = List.of(pay);
        when(payMapper.findAll()).thenReturn(payList);

        List<PayResponse> responses = payService.findAllPay();

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getId()).isEqualTo(pay.getId());
        verify(payMapper).findAll();
    }

    @Test
    @DisplayName("내가 결제한 결제 조회")
    void findAllUserPay() {
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("test");
        when(userMapper.findByLoginId("test")).thenReturn(user);

        List<Pay> payList = List.of(pay);
        when(payMapper.findByUserId(user.getId())).thenReturn(payList);

        List<PayResponse> responses = payService.findAllUserPay();

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getUserId()).isEqualTo(user.getId());
        verify(payMapper).findByUserId(user.getId());
    }

    @Test
    @DisplayName("결제 취소")
    void cancelPay() {
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("test");

        // pay 상태가 COMPLETE 임
        when(payMapper.findById(100L)).thenReturn(pay);
        when(userMapper.findByLoginId(anyString())).thenReturn(user);

        payService.cancelPay(100L);

        verify(orderMapper).orderStatus("CANCEL", order.getId());
        verify(payMapper).updatePay(argThat(pay -> pay.getPayStatus().equals("CANCEL")));
        verify(userMapper).updateUser(user);
    }




    @Test
    @DisplayName("결제 취소 실패 (회원 일치하지 않음)")
    void cancelPayFAIL() {
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("test");

        pay.setPayStatus("COMPLETE");
        when(payMapper.findById(100L)).thenReturn(pay);
        User User1 = new User();
        User1.setId(999L);
        when(userMapper.findByLoginId(anyString())).thenReturn(User1);

        assertThatThrownBy(() -> payService.cancelPay(100L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(NOT_MATCH_USER.getMessage());
    }

    @Test
    @DisplayName("결제 생성")
    void processPay() {
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("test");
        when(userMapper.findByLoginId("test")).thenReturn(user);

        // 주문 조회
        when(orderMapper.findOrderById(order.getId())).thenReturn(order);
        when(payMapper.countCompletePay(order.getId(), user.getId())).thenReturn(0);

        doNothing().when(payMapper).savePay(any(Pay.class));
        doNothing().when(orderMapper).orderStatus(anyString(), eq(order.getId()));

        PayCreateRequest request = PayCreateRequest.builder()
                .orderId(order.getId())
                .cardCompany("VISA")
                .cardNumber("1234-5678-9012-3456")
                .build();

        payService.processPay(request);

        verify(payMapper, times(1)).savePay(any(Pay.class));
        verify(orderMapper, times(1)).orderStatus(anyString(), eq(order.getId()));

    }
}

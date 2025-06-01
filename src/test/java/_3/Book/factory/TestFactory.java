package _3.Book.factory;

import book.Book.book.domain.Book;
import book.Book.order.domain.Order;
import book.Book.order.domain.OrderItem;
import book.Book.order.dto.OrderCreateRequest;
import book.Book.pay.domain.Pay;
import book.Book.user.domain.Cart;
import book.Book.user.domain.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class TestFactory {

    public static User createUser() {
        return User.builder()
                .id(1L)
                .loginId("hong")
                .username("홍길동")
                .build();
    }

    public static Book createBook() {
        return Book.builder()
                .id(1L)
                .price(10000)
                .build();
    }

    public static Cart createCart(User user, Book book, int amount) {
        return Cart.builder()
                .id(1L)
                .user(user)
                .bookId(book.getId())
                .totalAmount(amount)
                .totalPrice(book.getPrice() * amount)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static Order createOrder() {
        return Order.builder()
                .id(1L)
                .userId(1L)
                .name("테스트 수취인")
                .zipcode("12345")
                .address("서울시 강남구")
                .requirement("빠른 배송")
                .totalPrice(30000)
                .usedMileage(1000)
                .orderStatus("COMPLETE")
                .impUid("imp_12345678")
                .merchantId("merchant_12345678")
                .originalPrice(31000)
                .createdAt(LocalDate.now())
                .orderStatus("COMPLETE")
                .usedMileage(1000)
                .originalPrice(10000)
                .build();
    }

    public static Pay createPay(User user, Order order) {
        // testPay 초기화
        return Pay.builder()
                .id(100L)
                .userId(user.getId())
                .order(order)
                .cardCompany("VISA")
                .cardNumber("1234-5678-9012-3456")
                .payPrice(order.getTotalPrice())
                .payStatus("COMPLETE")
                .createdAt(LocalDate.now())
                .build();
    }

}

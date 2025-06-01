package book.Book.order.service;

import book.Book.order.dto.OrderCreateRequest;
import book.Book.order.dto.OrderResponse;

import java.util.List;

public interface OrderService {

    // 주문 생성
    void createOrder(OrderCreateRequest orderCreateRequest);

    // 주문 조회
    List<OrderResponse> orderFindUser();

    // 주문 단건 조회
    OrderResponse orderFindOne(Long orderId);

    // 회원 내 주문 조회
    List<OrderResponse> orderUserMyInfo();

    // 주문 취소
    void orderCancel(Long orderId);
}

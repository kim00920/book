package book.Book.order.controller;

import book.Book.order.dto.OrderResponse;
import book.Book.order.service.OrderService;
import book.Book.order.dto.OrderCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // 주문 생성
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String createOrder(@RequestBody OrderCreateRequest orderCreateRequest) {
        orderService.createOrder(orderCreateRequest);
       return "주문 생성이 완료됐습니다";
    }

    // 주문 단건 조회
    @GetMapping("/{orderId}")
    @ResponseStatus(HttpStatus.OK)
    public OrderResponse getOrder(@PathVariable("orderId") Long orderId) {
       return orderService.orderFindOne(orderId);
    }

    // 주문 전체 조회
    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public List<OrderResponse> getAllOrders() {
      return orderService.orderFindUser();
    }

    // 현재 내가 주문한 주문 조회
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<OrderResponse> orderUserMyInfo() {
        return orderService.orderUserMyInfo();
    }
    
    // 주문 취소
    @PutMapping("/{orderId}")
    @ResponseStatus(HttpStatus.OK)
    public String cancelOrder(@PathVariable("orderId") Long orderId) {
        orderService.orderCancel(orderId);
        return "주문 취소가 완료됐습니다";
    }
}
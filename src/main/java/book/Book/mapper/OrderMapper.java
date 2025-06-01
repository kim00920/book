package book.Book.mapper;

import book.Book.order.domain.Order;
import book.Book.order.domain.OrderItem;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface OrderMapper {

    // 주문 생성
    void insertOrder(Order order);

    // 주문아이템 생성
    void insertOrderItems(@Param("orderId") Long orderId, @Param("orderItems") List<OrderItem> orderItems);

    // 주문 단건 조회
    Order findOrderById(Long id);

    // 주문 상태 변경
    void orderStatus(@Param("orderStatus") String orderStatus, @Param("id") Long id);

    // 주문 유효성 검사 userId 랑 bookId 로 where 절 걸어서 있으면 1이상 없으면 0
    int findByUserAndBook(@Param("userId") Long userId, @Param("bookId") Long bookId);

    // 주문 번호에 해당 하는 주문아이템 전체 조회
    List<OrderItem> findOrderItemsByOrderId(Long orderId);

    // 주문 전체 조회
    List<Order> findAllOrders();

    // 주문 번호를 여러개 받아서 주문아이템 전체 조회
    List<OrderItem> findOrderItemsByOrderIds(@Param("orderIds") List<Long> orderIds);

    // 특정 회원의 주문 전체 조회
    List<Order> findOrdersByUserId(Long userId);
}

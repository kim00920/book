package book.Book.order.dto;

import book.Book.order.domain.Order;
import book.Book.order.domain.OrderItem;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {

    private Long orderId;
    private List<Item> items;
    private int usedMileage;
    private int totalPrice;
    private LocalDate orderDate;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Item {
        private Long bookId;
        private String bookName;
        private int quantity;
        private int pricePerItem;
        private int totalPrice; // quantity * pricePerItem
    }

    // toDto 메서드는 여기다!!
    public static OrderResponse toDto(Order order, List<OrderItem> orderItems) {
        List<Item> itemDtos = orderItems.stream()
                .map(orderItem -> Item.builder()
                        .bookId(orderItem.getBookId())
                        .bookName(orderItem.getBookTitle())
                        .quantity(orderItem.getQuantity())
                        .pricePerItem(orderItem.getPrice())
                        .totalPrice(orderItem.getQuantity() * orderItem.getPrice())
                        .build())
                .toList();

        return OrderResponse.builder()
                .orderId(order.getId())
                .items(itemDtos)
                .usedMileage(order.getUsedMileage())
                .totalPrice(order.getTotalPrice())
                .orderDate(order.getCreatedAt())
                .build();
    }
}

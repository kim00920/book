package book.Book.user.dto;

import book.Book.user.domain.Cart;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class CartResponse {
    private Long userId;
    private String userName;
    private Long bookId;
    private int totalAmount;
    private int totalPrice;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime createdAt;


    public static CartResponse toDto(Cart cart) {
        return CartResponse.builder()
                .userId(cart.getUser().getId())
                .userName(cart.getUser().getUsername())
                .bookId(cart.getBookId())
                .totalAmount(cart.getTotalAmount())
                .totalPrice(cart.getTotalPrice())
                .createdAt(cart.getCreatedAt())
                .build();
    }
}

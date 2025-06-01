package book.Book.user.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartUpdateRequest {
    private Long cartId;
    private Long bookId;
    private int amount;
}

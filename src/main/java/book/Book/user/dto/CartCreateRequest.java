package book.Book.user.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartCreateRequest {
    private Long bookId;
    private int amount;
}

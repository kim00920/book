package book.Book.user.domain;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {
    private Long id;
    private Long userId;
    private Long bookId;
    private String content;
}

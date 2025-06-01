package book.Book.user.domain;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rating {
    private Long id;
    private Long reviewId;
    private Double score;
}

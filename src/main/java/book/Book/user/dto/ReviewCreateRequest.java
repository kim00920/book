package book.Book.user.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewCreateRequest {

    private Long bookId;
    private String content;
    private RatingCreateRequest rating;

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    public static class RatingCreateRequest {
        private Double score;
    }
}

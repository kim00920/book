package book.Book.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ReviewUpdateRequest {

    private Long reviewId;
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

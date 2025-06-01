package book.Book.user.dto;

import book.Book.user.domain.Rating;
import book.Book.user.domain.Review;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewResponse {
    private Long reviewId;
    private Long userId;
    private Long bookId;
    private String content;
    private Double score;

    public static ReviewResponse toDto(Review review, Rating rating) {
        return ReviewResponse.builder()
                .reviewId(review.getId())
                .userId(review.getUserId())
                .bookId(review.getBookId())
                .content(review.getContent())
                .score(rating.getScore())
                .build();
    }
}
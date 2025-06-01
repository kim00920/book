package book.Book.user.controller;

import book.Book.user.dto.RatingResponse;
import book.Book.user.service.RatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rating")
@RequiredArgsConstructor
public class RatingController {

    private final RatingService ratingService;

    // 책 별점 그룹 조회
    @GetMapping("/{bookId}")
    @ResponseStatus(HttpStatus.OK)
    public List<RatingResponse> getGroupedRatings(
            @PathVariable("bookId") Long bookId) {
        return ratingService.countGroupedRating(bookId);
    }

    // 책 평균 별점 조회
    @GetMapping("/{bookId}/avg")
    @ResponseStatus(HttpStatus.OK)
    public Double getAverageRating(@PathVariable("bookId") Long bookId) {
        return ratingService.AvgRating(bookId);
    }

    // 특정 사용자의 책 별점 조회
    @GetMapping("/user/{userId}/book/{bookId}")
    public RatingResponse getUserRating(
            @PathVariable("userId") Long userId,
            @PathVariable("bookId") Long bookId) {
        return ratingService.findUserRating(userId, bookId);
    }
}

package book.Book.user.controller;

import book.Book.user.dto.ReviewCreateRequest;
import book.Book.user.dto.ReviewResponse;
import book.Book.user.dto.ReviewUpdateRequest;
import book.Book.user.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/review")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    // 리뷰 + 별점 등록
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String createReview(@RequestBody ReviewCreateRequest request) {
        reviewService.insertReview(request);
        return "리뷰 등록이 완료됐습니다";
    }

    // 리뷰 + 별점 수정
    @PutMapping("/{reviewId}")
    @ResponseStatus(HttpStatus.OK)
    public String updateReview(@RequestBody ReviewUpdateRequest request) {
        reviewService.updateReview(request);
        return "리뷰 수정이 완료됐습니다";
    }

    // 리뷰 + 별점 삭제
    @DeleteMapping("/{reviewId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public String deleteReview(@PathVariable Long reviewId) {
        reviewService.deleteReview(reviewId);
        return "리뷰 삭제가 완료됐습니다";
    }

    // 리뷰 + 별점 조회
    @GetMapping("/{reviewId}")
    @ResponseStatus(HttpStatus.OK)
    public ReviewResponse getReview(@PathVariable Long reviewId) {
        return reviewService.reviewFindOne(reviewId);
    }

    // 리뷰 + 별점 전체 조회
    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public List<ReviewResponse> getAllReviews() {
        return reviewService.reviewFindAll();
    }

    // 내가 등록한 리뷰 + 별점 조회
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ReviewResponse> getReviewByUser() {
        return reviewService.reviewFindAllByUser();
    }
}

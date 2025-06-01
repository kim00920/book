package book.Book.user.service.impl;

import book.Book.book.domain.Book;
import book.Book.error.BusinessException;
import book.Book.mapper.BookMapper;
import book.Book.mapper.OrderMapper;
import book.Book.mapper.ReviewMapper;
import book.Book.mapper.UserMapper;
import book.Book.user.domain.Rating;
import book.Book.user.domain.Review;
import book.Book.user.domain.User;
import book.Book.user.dto.ReviewCreateRequest;
import book.Book.user.dto.ReviewResponse;
import book.Book.user.service.ReviewService;
import book.Book.user.dto.ReviewUpdateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static book.Book.error.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ReviewServiceImpl implements ReviewService {

    private final UserMapper userMapper;
    private final BookMapper bookMapper;
    private final OrderMapper orderMapper;
    private final ReviewMapper reviewMapper;

    // 리뷰 + 별점 등록
    @Override
    public void insertReview(ReviewCreateRequest request) {
        log.info("리뷰 등록 요청: {}", request);
        User user = getUser();
        Book book = bookMapper.findById(request.getBookId());

        if (book == null) {
            log.warn("책을 찾을 수 없음. bookId={}", request.getBookId());
            throw new BusinessException(NOT_FOUND_BOOK);
        }

        int count = orderMapper.findByUserAndBook(user.getId(), request.getBookId());

        if (count < 1) {
            log.warn("책을 구매하지 않음. userId={}, bookId={}", user.getId(), request.getBookId());
            throw new BusinessException(NOT_BUY_BOOK);
        }

        Review review = Review.builder()
                .userId(user.getId())
                .bookId(request.getBookId())
                .content(request.getContent())
                .build();

        reviewMapper.insertReview(review);
        log.info("리뷰 저장 완료: reviewId={}", review.getId());

        Rating rating = Rating.builder()
                .score(request.getRating().getScore())
                .reviewId(review.getId())
                .build();

        reviewMapper.insertRating(rating);
        log.info("별점 저장 완료: reviewId={}, score={}", review.getId(), rating.getScore());
    }

    // 리뷰 + 별점 삭제
    @Override
    public void deleteReview(Long id) {
        log.info("리뷰 삭제 요청: reviewId={}", id);
        reviewMapper.deleteRating(id);
        reviewMapper.deleteReview(id);
        log.info("리뷰 및 별점 삭제 완료: reviewId={}", id);
    }

    // 리뷰 + 별점 수정
    @Override
    public void updateReview(ReviewUpdateRequest request) {
        log.info("리뷰 수정 요청: {}", request);
        User user = getUser();
        Book book = bookMapper.findById(request.getBookId());

        if (book == null) {
            log.warn("책을 찾을 수 없음. bookId={}", request.getBookId());
            throw new BusinessException(NOT_FOUND_BOOK);
        }

        int count = orderMapper.findByUserAndBook(user.getId(), request.getBookId());

        if (count < 1) {
            log.warn("책을 구매하지 않음. userId={}, bookId={}", user.getId(), request.getBookId());
            throw new BusinessException(NOT_BUY_BOOK);
        }

        Review existingReview = reviewMapper.findReview(request.getReviewId());
        if (existingReview == null) {
            log.warn("기존 리뷰 없음. reviewId={}", request.getReviewId());
            throw new BusinessException(NOT_FOUND_REVIEW);
        }

        if (!existingReview.getUserId().equals(user.getId())) {
            log.warn("리뷰 작성자 불일치. 로그인 userId={}, 리뷰 userId={}", user.getId(), existingReview.getUserId());
            throw new BusinessException(NOT_MATCH_USER);
        }

        Review review = Review.builder()
                .id(request.getReviewId())
                .userId(user.getId())
                .bookId(request.getBookId())
                .content(request.getContent())
                .build();

        reviewMapper.updateReview(review);
        log.info("리뷰 수정 완료: reviewId={}", request.getReviewId());

        Rating rating = Rating.builder()
                .reviewId(request.getReviewId())
                .score(request.getRating().getScore())
                .build();

        reviewMapper.updateRating(rating);
        log.info("별점 수정 완료: reviewId={}, score={}", request.getReviewId(), rating.getScore());
    }

    @Override
    @Transactional(readOnly = true)
    public ReviewResponse reviewFindOne(Long id) {
        log.info("리뷰 단건 조회 요청: reviewId={}", id);
        Review review = reviewMapper.findReview(id);
        Rating rating = reviewMapper.findRating(id);

        log.info("리뷰 조회 완료: review={}, rating={}", review, rating);
        return ReviewResponse.toDto(review, rating);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponse> reviewFindAll() {
        log.info("리뷰 전체 조회 요청");
        List<Review> reviews = reviewMapper.findAllReviews();

        List<ReviewResponse> responses = reviews.stream()
                .map(review -> {
                    Rating rating = reviewMapper.findRatingByReviewId(review.getId());
                    return ReviewResponse.toDto(review, rating);
                })
                .collect(Collectors.toList());

        log.info("전체 리뷰 수: {}", responses.size());
        return responses;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponse> reviewFindAllByUser() {
        log.info("사용자 리뷰 전체 조회 요청");

        User user = getUser(); // 현재 로그인한 사용자 정보 조회
        List<Review> reviews = reviewMapper.findReviewsByUserId(user.getId()); // 사용자 리뷰 조회

        List<ReviewResponse> responses = reviews.stream()
                .map(review -> {
                    Rating rating = reviewMapper.findRatingByReviewId(review.getId());
                    return ReviewResponse.toDto(review, rating);
                })
                .collect(Collectors.toList());

        log.info("사용자({}) 리뷰 수: {}", user.getLoginId(), responses.size());
        return responses;
    }

    private User getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loginId = authentication.getName();
        log.debug("현재 로그인 사용자: {}", loginId);

        User user = userMapper.findByLoginId(loginId);

        if (user == null) {
            log.error("로그인한 사용자를 찾을 수 없음: loginId={}", loginId);
            throw new BusinessException(NOT_FOUND_USER);
        }

        return user;
    }
}
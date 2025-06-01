package _3.Book.review;

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
import book.Book.user.dto.ReviewUpdateRequest;
import book.Book.user.service.ReviewService;
import _3.Book.factory.TestFactory;
import book.Book.user.service.impl.ReviewServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static book.Book.error.ErrorCode.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@DisplayName("리뷰 단위테스트")
class ReviewServiceTest {

    @InjectMocks
    ReviewServiceImpl reviewService;

    @Mock
    UserMapper userMapper;

    @Mock
    BookMapper bookMapper;

    @Mock
    OrderMapper orderMapper;

    @Mock
    ReviewMapper reviewMapper;

    private User user;
    private Book book;

    @BeforeEach
    void setUp() {

        user = TestFactory.createUser();
        book = TestFactory.createBook();

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user.getLoginId(), null)
        );
    }

    @Test
    @DisplayName("리뷰 등록")
    void reviewCreateOK() {
        ReviewCreateRequest request = ReviewCreateRequest.builder()
                .bookId(book.getId())
                .content("좋은 책입니다.")
                .rating(ReviewCreateRequest.RatingCreateRequest.builder().score(4.0).build())
                .build();

        when(userMapper.findByLoginId(user.getLoginId())).thenReturn(user);
        when(bookMapper.findById(book.getId())).thenReturn(book);
        when(orderMapper.findByUserAndBook(user.getId(), book.getId())).thenReturn(1);

        doAnswer(invocation -> {
            Review review = invocation.getArgument(0);
            review.setId(1L); // insert 후 ID가 셋팅된다고 가정
            return null;
        }).when(reviewMapper).insertReview(any(Review.class));

        doNothing().when(reviewMapper).insertRating(any(Rating.class));

        assertDoesNotThrow(() -> reviewService.insertReview(request));

        verify(reviewMapper, times(1)).insertReview(any(Review.class));
        verify(reviewMapper, times(1)).insertRating(any(Rating.class));
    }

    @Test
    @DisplayName("리뷰 등록 실패 (해당 책이 아님)")
    void reviewCreateFail_NotFoundBook() {
        ReviewCreateRequest request = ReviewCreateRequest.builder()
                .bookId(999L)
                .content("책 없음")
                .rating(ReviewCreateRequest.RatingCreateRequest.builder().score(4.0).build())
                .build();

        when(userMapper.findByLoginId(user.getLoginId())).thenReturn(user);
        when(bookMapper.findById(999L)).thenReturn(null);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> reviewService.insertReview(request));

        assertEquals(NOT_FOUND_BOOK, exception.getErrorCode());
    }

    @Test
    @DisplayName("리뷰 등록 실패 (구매 하지 않은 책)")
    void reviewCreateFail_NotBuyBook() {
        ReviewCreateRequest request = ReviewCreateRequest.builder()
                .bookId(book.getId())
                .content("구매 안함")
                .rating(ReviewCreateRequest.RatingCreateRequest.builder().score(4.0).build())
                .build();

        when(userMapper.findByLoginId(user.getLoginId())).thenReturn(user);
        when(bookMapper.findById(book.getId())).thenReturn(book);
        when(orderMapper.findByUserAndBook(user.getId(), book.getId())).thenReturn(0);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> reviewService.insertReview(request));

        assertEquals(NOT_BUY_BOOK, exception.getErrorCode());
    }

    @Test
    @DisplayName("리뷰 삭제")
    void deleteReviewOK() {
        Long reviewId = 1L;

        doNothing().when(reviewMapper).deleteRating(reviewId);
        doNothing().when(reviewMapper).deleteReview(reviewId);

        assertDoesNotThrow(() -> reviewService.deleteReview(reviewId));

        verify(reviewMapper).deleteRating(reviewId);
        verify(reviewMapper).deleteReview(reviewId);
    }

    @Test
    @DisplayName("리뷰 수정")
    void updateReviewOK() {
        ReviewUpdateRequest request = ReviewUpdateRequest.builder()
                .reviewId(1L)
                .bookId(book.getId())
                .content("수정된 내용")
                .rating(ReviewUpdateRequest.RatingCreateRequest.builder().score(4.0).build())
                .build();

        Review existingReview = Review.builder()
                .id(1L)
                .userId(user.getId())
                .bookId(book.getId())
                .content("기존 내용")
                .build();

        when(userMapper.findByLoginId(user.getLoginId())).thenReturn(user);
        when(bookMapper.findById(book.getId())).thenReturn(book);
        when(orderMapper.findByUserAndBook(user.getId(), book.getId())).thenReturn(1);
        when(reviewMapper.findReview(request.getReviewId())).thenReturn(existingReview);
        doNothing().when(reviewMapper).updateReview(any(Review.class));
        doNothing().when(reviewMapper).updateRating(any(Rating.class));

        assertDoesNotThrow(() -> reviewService.updateReview(request));

        verify(reviewMapper).updateReview(any(Review.class));
        verify(reviewMapper).updateRating(any(Rating.class));
    }

    @Test
    @DisplayName("리뷰 수정 실패 (존재하지 않는 리뷰)")
    void updateReviewFail_NotFoundReview() {
        ReviewUpdateRequest request = ReviewUpdateRequest.builder()
                .reviewId(99L)
                .bookId(book.getId())
                .content("내용")
                .rating(ReviewUpdateRequest.RatingCreateRequest.builder().score(4.0).build())

                .build();

        when(userMapper.findByLoginId(user.getLoginId())).thenReturn(user);
        when(bookMapper.findById(book.getId())).thenReturn(book);
        when(orderMapper.findByUserAndBook(user.getId(), book.getId())).thenReturn(1);
        when(reviewMapper.findReview(request.getReviewId())).thenReturn(null);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> reviewService.updateReview(request));

        assertEquals(NOT_FOUND_REVIEW, exception.getErrorCode());
    }


    @Test
    @DisplayName("리뷰 단건 조회")
    void reviewFindOneOK() {
        Long reviewId = 1L;
        Review review = Review.builder()
                .id(reviewId)
                .userId(user.getId())
                .bookId(book.getId())
                .content("내용")
                .build();

        Rating rating = Rating.builder()
                .reviewId(reviewId)
                .score(5.0)
                .build();

        when(reviewMapper.findReview(reviewId)).thenReturn(review);
        when(reviewMapper.findRating(reviewId)).thenReturn(rating);

        ReviewResponse response = reviewService.reviewFindOne(reviewId);

        assertNotNull(response);
        assertEquals(reviewId, response.getReviewId());
    }

    @Test
    @DisplayName("리뷰 전체 조회")
    void reviewFindAllOK() {
        Review review1 = Review.builder().id(1L).build();
        Review review2 = Review.builder().id(2L).build();

        Rating rating1 = Rating.builder().reviewId(1L).score(5.0).build();
        Rating rating2 = Rating.builder().reviewId(2L).score(3.0).build();

        when(reviewMapper.findAllReviews()).thenReturn(List.of(review1, review2));
        when(reviewMapper.findRatingByReviewId(1L)).thenReturn(rating1);
        when(reviewMapper.findRatingByReviewId(2L)).thenReturn(rating2);

        List<ReviewResponse> responses = reviewService.reviewFindAll();

        assertEquals(2, responses.size());

    }

    @Test
    @DisplayName("내가 등록한 리뷰 조회")
    void reviewFindAllByUserOK() {
        // given
        Review review1 = Review.builder().id(1L).userId(user.getId()).build();
        Review review2 = Review.builder().id(2L).userId(user.getId()).build();

        Rating rating1 = Rating.builder().reviewId(1L).score(5.0).build();
        Rating rating2 = Rating.builder().reviewId(2L).score(4.0).build();

        when(userMapper.findByLoginId(user.getLoginId())).thenReturn(user);
        when(reviewMapper.findReviewsByUserId(user.getId())).thenReturn(List.of(review1, review2));
        when(reviewMapper.findRatingByReviewId(1L)).thenReturn(rating1);
        when(reviewMapper.findRatingByReviewId(2L)).thenReturn(rating2);

        // when
        List<ReviewResponse> responses = reviewService.reviewFindAllByUser();

        // then
        assertEquals(2, responses.size());
        assertEquals(1L, responses.get(0).getReviewId());
        assertEquals(2L, responses.get(1).getReviewId());
    }

}

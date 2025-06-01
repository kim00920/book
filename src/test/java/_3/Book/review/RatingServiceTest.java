package _3.Book.review;

import book.Book.mapper.RatingMapper;
import book.Book.user.dto.RatingResponse;
import book.Book.user.service.impl.RatingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("별점 단위테스트")
class RatingServiceTest {


    @Mock
    private RatingMapper ratingMapper;

    @InjectMocks
    private RatingServiceImpl ratingService;


    @Test
    @DisplayName("책별 그룹화된 별점 개수 조회")
    void countGroupedRating_Success() {
        Long bookId = 1L;

        List<RatingResponse> mockResponse = List.of(
                new RatingResponse(5, 10), // 별점 5점이 10개
                new RatingResponse(4, 5)   // 별점 4점이 5개
        );

        when(ratingMapper.countGroupedRating(bookId)).thenReturn(mockResponse);

        List<RatingResponse> result = ratingService.countGroupedRating(bookId);

        assertNotNull(result);
        assertEquals(2, result.size());

        verify(ratingMapper, times(1)).countGroupedRating(bookId);
    }

    @Test
    @DisplayName("책별 평균 별점 조회")
    void avgRating_Success() {
        Long bookId = 1L;
        Double avg = 4.5;

        when(ratingMapper.avgRating(bookId)).thenReturn(avg);

        Double result = ratingService.AvgRating(bookId);

        assertNotNull(result);
        assertEquals(avg, result);

        verify(ratingMapper, times(1)).avgRating(bookId);
    }

    @Test
    @DisplayName("사용자의 책 별점 조회")
    void findUserRating_Success() {
        Long userId = 1L;
        Long bookId = 1L;

        RatingResponse mockResponse = new RatingResponse(5, 1);

        when(ratingMapper.findUserRating(userId, bookId)).thenReturn(mockResponse);

        RatingResponse result = ratingService.findUserRating(userId, bookId);

        assertNotNull(result);

        verify(ratingMapper, times(1)).findUserRating(userId, bookId);
    }
}

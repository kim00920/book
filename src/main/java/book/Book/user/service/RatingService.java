package book.Book.user.service;

import book.Book.user.dto.RatingResponse;

import java.util.List;

public interface RatingService {

        // 특정 책에 대한 별점 그룹핑 조회
        List<RatingResponse> countGroupedRating(Long bookId);

        // 특정 책의 평균 별점 조회
        Double AvgRating(Long bookId);

        // 특정 책에 대해서 사용자가 남긴 별점을 조회
        RatingResponse findUserRating(Long userId, Long bookId);
}

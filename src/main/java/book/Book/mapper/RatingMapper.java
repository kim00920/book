package book.Book.mapper;

import book.Book.user.dto.RatingResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RatingMapper {

    // 별점 그룹핑 조회
    List<RatingResponse> countGroupedRating(Long bookId);

    // 책 평균 별점
    Double avgRating(Long bookId);

    // 특정 사용자의 책 별점
    RatingResponse findUserRating(@Param("userId") Long userId, @Param("bookId") Long bookId);

}

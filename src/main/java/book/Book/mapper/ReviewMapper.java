package book.Book.mapper;

import book.Book.user.domain.Rating;
import book.Book.user.domain.Review;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Mapper
public interface ReviewMapper {

    // 리뷰 생성
    @Insert("INSERT INTO review (user_id, book_id, content) VALUES (#{userId}, #{bookId}, #{content})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertReview(Review review);

    // 별점 생성
    @Insert("INSERT INTO rating (review_id, score) VALUES (#{reviewId}, #{score})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertRating(Rating rating);

    // 리뷰 삭제
    @Delete("DELETE FROM review WHERE id = #{id}")
    void deleteReview(Long id);

    // 별점 삭제
    @Delete("DELETE FROM rating WHERE id = #{id}")
    void deleteRating(Long id);

    // 리뷰 단건 조회
    @Select("SELECT * FROM review WHERE id = #{id}}")
    Review findReview(Long id);

    // 별점 단건 조회
    @Select("SELECT * FROM rating WHERE id = #{id}")
    Rating findRating(Long id);

    // 리뷰 수정
    @Update(" UPDATE reviewSET content = #{content} WHERE id = #{id}")
    void updateReview(Review review);

    // 별점 수정
    @Update("UPDATE rating SET score = #{score} WHERE review_id = #{reviewId}")
    void updateRating(Rating rating);

    // 리뷰 전체 조회
    @Select("SELECT * FROM review")
    List<Review> findAllReviews();

    // 별점 조회 할떄 그 리뷰 id로 가져옴
    @Select("SELECT * FROM rating WHERE review_id = #{reviewId}")
    Rating findRatingByReviewId(Long reviewId);

    // 특정 사용자의 별점 전체 조회
    @Select("SELECT * FROM review WHERE user_id = #{id}")
    List<Review> findReviewsByUserId(Long id);
}


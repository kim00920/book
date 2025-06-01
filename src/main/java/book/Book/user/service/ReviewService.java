package book.Book.user.service;

import book.Book.user.dto.ReviewCreateRequest;
import book.Book.user.dto.ReviewResponse;
import book.Book.user.dto.ReviewUpdateRequest;

import java.util.List;

public interface ReviewService {

    // 리뷰 + 별점 생성
    void insertReview(ReviewCreateRequest request);

    // 리뷰 + 별점 삭제
    void deleteReview(Long id);

    // 리뷰 + 별점 수정
    void updateReview(ReviewUpdateRequest request);

    // 리뷰 + 별점 단건 조회
    ReviewResponse reviewFindOne(Long id);

    // 리뷰 + 별점 전체 조회
    List<ReviewResponse> reviewFindAll();

    // 자신이 적은 리뷰 확인
    List<ReviewResponse> reviewFindAllByUser();



}

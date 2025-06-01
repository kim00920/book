package book.Book.user.service;

import book.Book.user.dto.CartCreateRequest;
import book.Book.user.dto.CartResponse;
import book.Book.user.dto.CartUpdateRequest;

import java.util.List;

public interface CartService {

    // 장바구니 생성
    void insertCart(CartCreateRequest request);

    // 장바구니 삭제
    void deleteCart(Long id);

    // 장바구니 수량 수정
    void updateCart(CartUpdateRequest request);

    // 전체 장바구니 확인
    List<CartResponse> cartFindAll();

    // 현재 내가 담은 장바구니 확인
    List<CartResponse> cartMyInfo();


}

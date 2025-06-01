package book.Book.user.controller;

import book.Book.user.dto.CartCreateRequest;
import book.Book.user.dto.CartResponse;
import book.Book.user.dto.CartUpdateRequest;
import book.Book.user.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    // 장바구니 생성
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String insertCart(@RequestBody CartCreateRequest request) {
        cartService.insertCart(request);
        return "장바구니에 담았습니다.";
    }

    // 장바구니 삭제
    @DeleteMapping("/{cartId}")
    @ResponseStatus(HttpStatus.OK)
    public String deleteCart(@PathVariable Long cartId) {
        cartService.deleteCart(cartId);
        return "장바구니에서 삭제했습니다.";
    }

    // 특정 장바구니 항목 수정
    @PutMapping("/{cartId}")
    @ResponseStatus(HttpStatus.OK)
    public String updateCart(@PathVariable Long cartId, @RequestBody CartUpdateRequest request) {
        request.setCartId(cartId);
        cartService.updateCart(request);
        return "장바구니가 수정되었습니다.";
    }
    // 장바구니 전체 조회
    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public List<CartResponse> getAllCarts() {
        return cartService.cartFindAll();
    }

    // 현재 내가 담은 장바구니 전체 조회
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CartResponse> getMyCart() {
        return cartService.cartMyInfo();
    }
}

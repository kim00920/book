package book.Book.user.service.impl;

import book.Book.book.domain.Book;
import book.Book.error.BusinessException;
import book.Book.mapper.BookMapper;
import book.Book.mapper.CartMapper;
import book.Book.mapper.UserMapper;
import book.Book.user.domain.Cart;
import book.Book.user.domain.User;
import book.Book.user.dto.CartCreateRequest;
import book.Book.user.dto.CartResponse;
import book.Book.user.dto.CartUpdateRequest;
import book.Book.user.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;  // 추가
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static book.Book.error.ErrorCode.*;

@Slf4j  // 로깅 활성화
@Service
@RequiredArgsConstructor
@Transactional
public class CartServiceImpl implements CartService {

    private final CartMapper cartMapper;
    private final UserMapper userMapper;
    private final BookMapper bookMapper;

    @Override
    public void insertCart(CartCreateRequest request) {
        log.info("insertCart 호출 - 요청 정보: {}", request);
        User user = getUser();
        log.info("현재 로그인 사용자: {}", user.getLoginId());

        Book book = bookMapper.findById(request.getBookId());
        if (book == null) {
            throw new BusinessException(NOT_FOUND_BOOK);
        }

        if (cartMapper.existByBook(book.getId(), user)) {
            throw new BusinessException(DUPLICATED_CART);
        }

        int bookTotalPrice = book.getPrice();

        Cart cart = Cart.builder()
                .user(user)
                .bookId(book.getId())
                .totalAmount(request.getAmount())
                .totalPrice(bookTotalPrice * request.getAmount())
                .createdAt(LocalDateTime.now())
                .build();

        cartMapper.insertCart(cart);
        log.info("장바구니 항목 추가 완료 - cart: {}", cart);
    }

    @Override
    public void deleteCart(Long id) {
        log.info("deleteCart 호출 - 삭제할 cartId: {}", id);
        Cart cart = cartMapper.findById(id);

        if (cart == null) {
            throw new BusinessException(NOT_FOUND_CART);
        }

        cartMapper.deleteCart(cart);
        log.info("장바구니 항목 삭제 완료 - cartId: {}", id);
    }

    @Override
    public void updateCart(CartUpdateRequest request) {
        log.info("updateCart 호출 - 요청 정보: {}", request);
        User user = getUser();

        Book book = bookMapper.findById(request.getBookId());

        if (book == null) {
            throw new BusinessException(NOT_FOUND_BOOK);
        }

        Cart cart = cartMapper.findByUserIdAndBookId(user.getId(), book.getId());


        if (cart == null) {
            throw new BusinessException(NOT_FOUND_CART);
        }


        cart.setTotalAmount(request.getAmount());
        cart.setTotalPrice(book.getPrice() * request.getAmount());

        cartMapper.updateCart(cart);
        log.info("장바구니 항목 수정 완료");
    }

    @Override
    @Transactional(readOnly = true)
    public List<CartResponse> cartFindAll() {
        log.info("cartFindAll 호출");
        List<Cart> cartList = cartMapper.findAllCart();

        log.info("총 {}개의 장바구니 항목 조회됨", cartList.size());
        return cartList.stream()
                .map(CartResponse::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CartResponse> cartMyInfo() {
        User user = getUser();
        log.info("cartMyInfo 호출 - 사용자: {}", user.getLoginId());

        List<Cart> carts = cartMapper.findAllByUser(user);
        log.info("사용자 {}의 장바구니 항목 {}개 조회", user.getLoginId(), carts.size());

        return carts.stream()
                .map(CartResponse::toDto)
                .toList();
    }

    private User getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loginId = authentication.getName();

        User user = userMapper.findByLoginId(loginId);

        if (user == null) {
            throw new BusinessException(NOT_FOUND_USER);
        }

        log.info("로그인 사용자 조회 성공 - loginId: {}", loginId);
        return user;
    }
}

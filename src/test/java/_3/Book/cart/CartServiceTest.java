package _3.Book.cart;

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
import book.Book.user.service.impl.CartServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static _3.Book.factory.TestFactory.*;
import static book.Book.error.ErrorCode.NOT_FOUND_BOOK;
import static book.Book.error.ErrorCode.NOT_FOUND_CART;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
@DisplayName("장바구니 단위테스트")
class CartServiceTest {

    @InjectMocks
    CartServiceImpl cartService;

    @Mock
    CartMapper cartMapper;

    @Mock
    UserMapper userMapper;

    @Mock
    BookMapper bookMapper;

    @BeforeEach
    void setupAuth() {
        Authentication authentication = new UsernamePasswordAuthenticationToken("hong", null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    @DisplayName("장바구니 추가")
    void insertCartOK() {
        User user = createUser();
        Book book = createBook();
        CartCreateRequest request = CartCreateRequest.builder()
                .bookId(book.getId())
                .amount(2)
                .build();

        given(userMapper.findByLoginId("hong")).willReturn(user);
        given(bookMapper.findById(book.getId())).willReturn(book);
        given(cartMapper.existByBook(book.getId(), user)).willReturn(false);

        cartService.insertCart(request);

        then(cartMapper).should().insertCart(argThat(cart ->
                cart.getUser().equals(user) &&
                        cart.getBookId().equals(book.getId()) &&
                        cart.getTotalAmount() == 2 &&
                        cart.getTotalPrice() == book.getPrice() * 2
        ));
    }

    @Test
    @DisplayName("장바구니 추가 실패 (책 없음)")
    void insertCartFail() {
        given(userMapper.findByLoginId("hong")).willReturn(createUser());
        given(bookMapper.findById(999L)).willReturn(null);

        CartCreateRequest request = CartCreateRequest.builder()
                .bookId(999L)
                .amount(1)
                .build();

        assertThatThrownBy(() -> cartService.insertCart(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(NOT_FOUND_BOOK.getMessage());
    }

    @Test
    @DisplayName("장바구니 삭제")
    void deleteCartOK() {
        Cart cart = createCart(createUser(), createBook(), 1);
        given(cartMapper.findById(1L)).willReturn(cart);

        cartService.deleteCart(1L);

        then(cartMapper).should().deleteCart(cart);
    }

    @Test
    @DisplayName("장바구니 삭제 실패 (존재하지 않는 장바구니)")
    void deleteCartFail() {
        given(cartMapper.findById(1L)).willReturn(null);

        assertThatThrownBy(() -> cartService.deleteCart(1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(NOT_FOUND_CART.getMessage());
    }

    @Test
    @DisplayName("장바구니 수정")
    void updateCartOK() {
        User user = createUser();
        Book book = createBook();
        Cart cart = createCart(user, book, 1);

        CartUpdateRequest request = CartUpdateRequest.builder()
                .cartId(cart.getId())
                .bookId(book.getId())
                .amount(3)
                .build();

        given(userMapper.findByLoginId("hong")).willReturn(user);
        given(bookMapper.findById(book.getId())).willReturn(book);
        given(cartMapper.findByUserIdAndBookId(user.getId(), book.getId())).willReturn(cart);

        cartService.updateCart(request);

        then(cartMapper).should().updateCart(cart);
        assertThat(cart.getTotalAmount()).isEqualTo(3);
        assertThat(cart.getTotalPrice()).isEqualTo(book.getPrice() * 3);
    }

    @Test
    @DisplayName("장바구니 수정 실패 (책 없음)")
    void updateCartFail() {
        User user = createUser();
        Cart cart = createCart(user, createBook(), 1);

        CartUpdateRequest request = CartUpdateRequest.builder()
                .cartId(cart.getId())
                .bookId(999L)  // 없는 책 ID
                .amount(2)
                .build();

        given(userMapper.findByLoginId("hong")).willReturn(user);
        given(bookMapper.findById(999L)).willReturn(null);

        assertThatThrownBy(() -> cartService.updateCart(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(NOT_FOUND_BOOK.getMessage());
    }

    @Test
    @DisplayName("장바구니 수정 실패 (장바구니 없음)")
    void updateCartFailNoCart() {
        User user = createUser();
        Book book = createBook();

        CartUpdateRequest request = CartUpdateRequest.builder()
                .cartId(1L)
                .bookId(book.getId())
                .amount(2)
                .build();

        given(userMapper.findByLoginId("hong")).willReturn(user);
        given(bookMapper.findById(book.getId())).willReturn(book);
        given(cartMapper.findByUserIdAndBookId(user.getId(), book.getId())).willReturn(null);

        assertThatThrownBy(() -> cartService.updateCart(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(NOT_FOUND_CART.getMessage());
    }

    @Test
    @DisplayName("전체 장바구니 조회")
    void cartFindAllOK() {
        User user = createUser();
        Cart cart1 = createCart(user, createBook(), 1);
        Cart cart2 = createCart(user, createBook(), 2);

        given(cartMapper.findAllCart()).willReturn(List.of(cart1, cart2));

        List<CartResponse> responses = cartService.cartFindAll();

        assertThat(responses).hasSize(2);
    }

    @Test
    @DisplayName("내 장바구니 조회")
    void cartMyInfoOK() {
        User user = createUser();
        Cart cart1 = createCart(user, createBook(), 1);
        Cart cart2 = createCart(user, createBook(), 2);

        given(userMapper.findByLoginId("hong")).willReturn(user);
        given(cartMapper.findAllByUser(user)).willReturn(List.of(cart1, cart2));

        List<CartResponse> responses = cartService.cartMyInfo();

        assertThat(responses).hasSize(2);
    }
}

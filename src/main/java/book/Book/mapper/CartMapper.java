package book.Book.mapper;

import book.Book.user.domain.Cart;
import book.Book.user.domain.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CartMapper {

    // 장바구니 생성
    @Insert("""
                INSERT INTO cart(user_id, book_id, total_amount, total_price, created_at)
                VALUES (#{user.id}, #{bookId}, #{totalAmount}, #{totalPrice}, #{createdAt})
            """)
    void insertCart(Cart cart);

    // 회원이 이미 장바구니에 담은 책인지?
    @Select("SELECT EXISTS(SELECT 1 FROM cart WHERE book_id = #{bookId} AND user_id = #{user.id})")
    boolean existByBook(@Param("bookId") Long bookId, @Param("user") User user);

    // 회원이 주문하면 해당 회원의 장바구니 책도 삭제함
    @Delete("DELETE FROM cart WHERE user_id = #{userId} AND book_id = #{bookId}")
    void deleteByUserIdAndBookId(@Param("userId") Long userId, @Param("bookId") Long bookId);

    // 회원이 장바구니에 담은 책인지?
    Cart findByUserIdAndBookId(@Param("userId") Long userId, @Param("bookId") Long bookId);

    // 장바구니 수정
    @Update("""
            UPDATE cart SET total_amount = #{totalAmount}, total_price = #{totalPrice}
            WHERE user_id = #{user.id} AND book_id = #{bookId}
            """)
    void updateCart(Cart cart);

    // 장바구니 단건 조회
    @Select("SELECT * FROM cart WHERE id = #{id}")
    Cart findById(Long id);

    // 장바구니 삭제
    @Delete("DELETE FROM cart WHERE id = #{id}")
    void deleteCart(Cart cart);

    // 장바구니 전체 조회
    List<Cart> findAllCart();

    // 내가 담은 장바구니 전체 조회
    List<Cart> findAllByUser(User user);
}

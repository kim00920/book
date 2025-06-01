package book.Book.mapper;

import book.Book.book.domain.Book;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface BookMapper {

    // 책 생성
    void insertBook(Book book);

    // 책 단건 조회
    Book findById(Long id);

    // 책 삭제
    @Delete("DELETE FROM book WHERE id= #{id}")
    void deleteBook(Long id);

    // 책 전체 조회
    List<Book> findAll();

    // 재고 감소
    @Update("UPDATE book SET amount = amount - #{quantity} WHERE id = #{bookId} AND amount >= #{quantity}")
    void decreaseBookAmount(@Param("bookId") Long bookId, @Param("quantity") int quantity);

    void updateBook(Book book);

    Book findByIsbn(String isbn);
}

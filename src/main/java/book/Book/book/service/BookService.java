package book.Book.book.service;

import book.Book.book.dto.BookResponse;
import book.Book.book.dto.BookUpdateRequest;

import java.util.List;

public interface BookService {

    // 책 생성
    void insertBook(String title);

    // 책 삭제
    void deleteBook(Long id);

    // 책 수정
    void updateBook(Long bookId, BookUpdateRequest request);

    // 책 전체 조회
    List<BookResponse> bookFindAll();

    // 책 단건 조회
    BookResponse bookFindOne(Long id);
}

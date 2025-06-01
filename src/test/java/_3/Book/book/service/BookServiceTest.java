package _3.Book.book.service;

import book.Book.book.domain.Book;
import book.Book.book.dto.BookResponse;
import book.Book.book.dto.BookUpdateRequest;
import book.Book.book.service.impl.BookServiceImpl;
import book.Book.error.BusinessException;
import book.Book.mapper.BookMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static book.Book.error.ErrorCode.NOT_FOUND_BOOK;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("책 단위테스트")
class BookServiceTest {

    @InjectMocks
    private BookServiceImpl bookService;

    @Mock
    private BookMapper bookMapper;

    @Test
    @DisplayName("책 단건 조회")
    void bookFindOneOK() {
        Book book = Book.builder()
                .id(1L)
                .title("자바의 정석")
                .author("남궁성")
                .isbn("1234567890")
                .publisher("도우출판")
                .price(30000)
                .amount(100)
                .bookStatus("정상판매")
                .build();

        given(bookMapper.findById(1L)).willReturn(book);

        BookResponse response = bookService.bookFindOne(1L);

        assertThat(response.getTitle()).isEqualTo("자바의 정석");
        verify(bookMapper).findById(1L);
    }

    @Test
    @DisplayName("책 단건 조회 실패 (존재하지 않는 책)")
    void bookFindOneFail() {
        given(bookMapper.findById(2L)).willReturn(null);

        assertThatThrownBy(() -> bookService.bookFindOne(2L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(NOT_FOUND_BOOK.getMessage());
    }

    @Test
    @DisplayName("책 수정")
    void bookUpdateOK() {
        Book existingBook = Book.builder()
                .id(1L)
                .isbn("1234567890")
                .title("제목")
                .author("작성자")
                .publisher("출판사")
                .price(10000)
                .amount(10)
                .bookStatus("정상판매")
                .build();

        BookUpdateRequest updateRequest = BookUpdateRequest.builder()
                .title("제목1")
                .author("작성자1")
                .publisher("출판사1")
                .price(15000)
                .amount(20)
                .bookStatus("정상판매")
                .build();

        given(bookMapper.findById(1L)).willReturn(existingBook);

        bookService.updateBook(1L, updateRequest);

        verify(bookMapper).updateBook(any(Book.class));
    }

    @Test
    @DisplayName("책 삭제")
    void bookDeleteOK() {
        Long bookId = 1L;
        willDoNothing().given(bookMapper).deleteBook(bookId);

        bookService.deleteBook(bookId);

        then(bookMapper).should().deleteBook(bookId);
    }

    @Test
    @DisplayName("책 전체 조회")
    void bookFindAll() {
        Book book1 = Book.builder()
                .id(1L)
                .title("책1")
                .author("저자1")
                .isbn("isbn1")
                .publisher("출판사1")
                .price(10000)
                .amount(10)
                .bookStatus("정상판매")
                .build();

        Book book2 = Book.builder()
                .id(2L)
                .title("책2")
                .author("저자2")
                .isbn("isbn2")
                .publisher("출판사2")
                .price(20000)
                .amount(20)
                .bookStatus("정상판매")
                .build();

        given(bookMapper.findAll()).willReturn(List.of(book1, book2));

        List<BookResponse> responseList = bookService.bookFindAll();

        assertThat(responseList).hasSize(2);
        assertThat(responseList.get(0).getTitle()).isEqualTo("책1");
        assertThat(responseList.get(1).getTitle()).isEqualTo("책2");
        then(bookMapper).should().findAll();
    }
}

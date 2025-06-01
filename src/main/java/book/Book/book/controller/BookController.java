package book.Book.book.controller;

import book.Book.book.dto.BookResponse;
import book.Book.book.dto.BookUpdateRequest;
import book.Book.book.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/book")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;


    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public String saveBook(@RequestParam("title") String title) {
        bookService.insertBook(title);
        return "생성 완료";
    }

    // 책 전체 조회
    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public List<BookResponse> findAllBooks() {
        return bookService.bookFindAll();
    }

    // 책 단건 조회
    @GetMapping("/{bookId}")
    @ResponseStatus(HttpStatus.OK)
    public BookResponse findOneBook(@PathVariable("bookId") Long bookId) {
        return bookService.bookFindOne(bookId);
    }

    // 책 삭제
    @DeleteMapping("/{bookId}")
    @ResponseStatus(HttpStatus.OK)
    public String deleteBook(@PathVariable("bookId") Long bookId) {
        bookService.deleteBook(bookId);
        return "삭제 성공";
    }

    // 책 수정
    @PutMapping("/{bookId}")
    @ResponseStatus(HttpStatus.OK)
    public String updateBook(@PathVariable("bookId") Long bookId,
                             @RequestBody BookUpdateRequest request) {
        bookService.updateBook(bookId, request);
        return "수정 완료";
    }
}
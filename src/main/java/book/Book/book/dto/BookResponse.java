package book.Book.book.dto;

import book.Book.book.domain.Book;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class BookResponse {

    private Long id;
    private String title;
    private String author;
    private String publisher;
    private String isbn;
    private int price;
    private int amount;
    private String bookStatus;

    public static BookResponse toDto(Book book) {
        return BookResponse.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .publisher(book.getPublisher())
                .isbn(book.getIsbn())
                .price(book.getPrice())
                .amount(book.getAmount())
                .bookStatus(book.getBookStatus() != null ? book.getBookStatus() : null)
                .build();
    }
}

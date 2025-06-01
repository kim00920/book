package book.Book.book.domain;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Book {
    private Long id;
    private String title;
    private String author;
    private String publisher;
    private String isbn;
    private int price;
    private int amount;
    private String bookStatus;
}

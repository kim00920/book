package book.Book.book.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookUpdateRequest {
    private String title;
    private String author;
    private String publisher;
    private int price;
    private int amount;
    private String bookStatus;
}
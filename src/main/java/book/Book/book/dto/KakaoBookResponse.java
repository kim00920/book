package book.Book.book.dto;

import lombok.Data;

import java.util.List;

@Data
public class KakaoBookResponse {
    private List<Document> documents;

    @Data
    public static class Document {
        private String title;
        private List<String> authors;
        private String publisher;
        private String isbn;
        private int price;
    }
}
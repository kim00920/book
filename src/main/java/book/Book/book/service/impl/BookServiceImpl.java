package book.Book.book.service.impl;

import book.Book.book.domain.Book;
import book.Book.book.dto.BookResponse;
import book.Book.book.dto.BookUpdateRequest;
import book.Book.book.dto.KakaoBookResponse;
import book.Book.book.service.BookService;
import book.Book.error.BusinessException;
import book.Book.mapper.BookMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static book.Book.error.ErrorCode.NOT_FOUND_BOOK;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class BookServiceImpl implements BookService {

    private final BookMapper bookMapper;

    @Value("${kakao.api.key}")
    private String kakaoApiKey;

    // 카카오 API 사용해서 DB에 책 정보 넣기
    @Override
    public void insertBook(String title) {
        log.info("insertBook 시작 - title: {}", title);

        RestTemplate restTemplate = new RestTemplate();
        String apiUrl = "https://dapi.kakao.com/v3/search/book?query=" + title;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + kakaoApiKey);

        log.info("Kakao API 요청 URL: {}", apiUrl);
        log.info("Kakao API Key: {}", kakaoApiKey);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<KakaoBookResponse> response = restTemplate.exchange(
                apiUrl, HttpMethod.GET, entity, KakaoBookResponse.class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            KakaoBookResponse kakaoBookResponse = response.getBody();

            if (!kakaoBookResponse.getDocuments().isEmpty()) {
                KakaoBookResponse.Document doc = kakaoBookResponse.getDocuments().get(0);
                log.info("API 응답 성공 - 책 제목: {}", doc.getTitle());


                Book extBook = bookMapper.findByIsbn(doc.getIsbn());
                if (extBook != null) {
                    throw new BusinessException(NOT_FOUND_BOOK);
                }

                Book book = Book.builder()
                        .title(doc.getTitle())
                        .author(String.join(", ", doc.getAuthors()))
                        .publisher(doc.getPublisher())
                        .isbn(doc.getIsbn())
                        .price(doc.getPrice())
                        .amount(1000)
                        .bookStatus("정상판매")
                        .build();

                bookMapper.insertBook(book);
                log.info("책 저장 완료 - title: {}", book.getTitle());
            } else {
                log.info("검색 결과가 없습니다 - title: {}", title);
            }
        } else {
            throw new RuntimeException("API 응답 실패");
        }
    }

    // 책 삭제
    @Override
    public void deleteBook(Long id) {
        log.info("책 삭제 요청 - id: {}", id);
        bookMapper.deleteBook(id);
        log.info("책 삭제 완료 - id: {}", id);
    }

    @Override
    public void updateBook(Long bookId, BookUpdateRequest request) {
        log.info("책 수정 요청 - id: {}", bookId);

        Book existingBook = bookMapper.findById(bookId);
        bookIsNull(existingBook);

        Book updatedBook = Book.builder()
                .id(existingBook.getId())  // ID 유지
                .isbn(existingBook.getIsbn()) // ISBN은 수정하지 않음
                .title(request.getTitle())
                .author(request.getAuthor())
                .publisher(request.getPublisher())
                .price(request.getPrice())
                .amount(request.getAmount())
                .bookStatus(request.getBookStatus())
                .build();

        bookMapper.updateBook(updatedBook);
        log.info("책 수정 완료 - id: {}", bookId);
    }

    // 책 전체 조회
    @Override
    @Transactional(readOnly = true)
    public List<BookResponse> bookFindAll() {
        log.info("전체 책 목록 조회 시작");
        List<Book> bookList = bookMapper.findAll();
        log.info("조회된 책 수: {}", bookList.size());

        return bookList.stream()
                .map(BookResponse::toDto)
                .toList();
    }

    // 책 1개 조회
    @Override
    @Transactional(readOnly = true)
    public BookResponse bookFindOne(Long id) {
        log.info("책 상세 조회 시작 - id: {}", id);
        Book book = bookMapper.findById(id);
        bookIsNull(book);
        log.info("조회 완료 - title: {}", book.getTitle());

        return BookResponse.toDto(book);
    }

    // 책 있는지?
    private boolean bookIsNull(Book book) {
        if (book == null) {
            throw new BusinessException(NOT_FOUND_BOOK);
        }
        return true;
    }
}

package book.Book.error;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<BusinessException> BusinessExceptionHandler(BusinessException e) {
        return ResponseEntity.badRequest().body(new BusinessException(e.getErrorCode()));
    }
}

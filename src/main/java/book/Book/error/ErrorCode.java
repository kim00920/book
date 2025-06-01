package book.Book.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // 회원
    NOT_FOUND_USER(HttpStatus.NOT_FOUND, "존재하지 않는 회원입니다."),
    DUPLICATED_LOGIN_ID(HttpStatus.BAD_REQUEST, "이미 존재하고 있는 아이디 입니다."),
    UNAUTHORIZED_ACCESS(HttpStatus.UNAUTHORIZED, "로그인된 사용자만 접근 가능합니다."),
    NOT_EQUALS_PASSWORD(HttpStatus.BAD_REQUEST, "현재 비밀번호가 일치하지 않습니다."),
    NOT_EQUALS_PASSWORD_CONFIRM(HttpStatus.BAD_REQUEST, "새 비밀번호 확인과 새 비밀번호가 일치하지 않습니다."),
    DUPLICATED_PASSWORD(HttpStatus.BAD_REQUEST, "같은 비밀번호는 사용 할 수 없습니다."),
    NOT_MATCH_PASSWORD(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다."),

    // 책
    NOT_FOUND_BOOK(HttpStatus.NOT_FOUND, "존재하지 않는 책입니다."),
    DUPLICATED_BOOK(HttpStatus.BAD_REQUEST, "이미 존재하고 있는 책 입니다."),


    // 리뷰
    NOT_BUY_BOOK(HttpStatus.BAD_REQUEST, "구매하지 않는 책에 대해서 리뷰를 남길수 없습니다."),
    NOT_MATCH_USER(HttpStatus.BAD_REQUEST, "회원이 일치하지 않습니다."),
    NOT_FOUND_REVIEW(HttpStatus.NOT_FOUND,  "존재하지 않는 리뷰입니다."),

    // 장바구니

   DUPLICATED_CART(HttpStatus.BAD_REQUEST,  "장바구니에 이미 담겨있습니다."),
    NOT_FOUND_CART(HttpStatus.BAD_REQUEST,  "존재하지 않는 장바구니 입니다."),
    TO_MANY_CART_QUANTITY(HttpStatus.BAD_REQUEST,"장바구니 수량보다 많은 수량을 주문할 수 없습니다."),

    // 주문

    NOT_FOUND_ORDER(HttpStatus.BAD_REQUEST,  "존재하지 않는 주문입니다."),
    DUPLICATED_ORDER(HttpStatus.BAD_REQUEST,  "이미 완료된 주문입니다."),

    // 마일리지
    NOT_ENOUGH_MILEAGE(HttpStatus.BAD_REQUEST,  "마일리지가 부족합니다."),

    // 결제
    NOT_FOUND_PAY(HttpStatus.BAD_REQUEST,  "존재하지 않는 결제입니다."),
    DUPLICATED_PAY(HttpStatus.BAD_REQUEST,  "이미 완료된 결제입니다."),
    ALREADY_CANCEL_ORDER(HttpStatus.BAD_REQUEST,  "이미 취소된 결제입니다."),
    ALREADY_FAIL_ORDER(HttpStatus.BAD_REQUEST,  "이미 실패된 결제입니다."),
    NOT_COMPLETE_PAY(HttpStatus.BAD_REQUEST,  "결제 완료된 내역만 취소가 가능합니다.");



    ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    private final HttpStatus httpStatus;
    private final String message;
}

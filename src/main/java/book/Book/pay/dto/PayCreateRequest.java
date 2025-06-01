package book.Book.pay.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class PayCreateRequest {

    private Long orderId;
    private String cardCompany;     // 카드사
    private String cardNumber;      // 카드일련번호
}

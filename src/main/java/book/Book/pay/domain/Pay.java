package book.Book.pay.domain;

import book.Book.order.domain.Order;
import lombok.*;
import org.apache.ibatis.mapping.FetchType;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pay {

    private Long id;                //결제번호(PK)

    private Long userId;          //회원번호

    private Order order;            //주문(일대일)

    private String cardCompany;     // 카드사

    private String cardNumber;      // 카드일련번호

    private int payPrice;          // 결제가격 (주문에 있는 TOTAL PRICE)

    private String payStatus;   // 결제상태 (COMPLETE , CANCEL)

    private LocalDate createdAt;
}

package book.Book.order.domain;

import lombok.*;
import org.apache.ibatis.mapping.FetchType;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {

    private Long id;               //주문상품번호(PK  )

    private Long userId;        // 회원 ID

    private Long bookId;         // 책

    private Long orderId;

    private String bookTitle;     // 책 이름

    private int price;            // 상품 가격

    private int quantity;

    private int orderPrice;      //주문수량가격


}

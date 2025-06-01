package book.Book.user.domain;

import lombok.*;
import org.apache.ibatis.mapping.FetchType;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cart {


    private Long id;        //장바구니 번호
    private User user;
    private Long bookId;
    private int totalAmount;    //장바구니 총 수량
    private int totalPrice;     //장바구니 총 가격
    private LocalDateTime createdAt;

}

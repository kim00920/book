package book.Book.order.domain;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Order {

    private Long id;                //주문번호(PK)
    private Long userId;          //회원번호
    private String name;            //수취인 이름
    private String zipcode;         //수취인 우편번호
    private String address;   //수취인 상세주소
    private String requirement;     //요청사항
    private int totalPrice;    //총 주문금액
    private int usedMileage;   // 사용할 마일리지
    private String orderStatus; //주문상태 (COMPLETE, CANCEL)
    private String impUid;
    private String merchantId;
    private int originalPrice;
    private LocalDate createdAt;

}

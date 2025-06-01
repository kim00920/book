package book.Book.pay.dto;

import book.Book.order.domain.Order;
import book.Book.pay.domain.Pay;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
public class PayResponse {

        private Long id;
        private Long userId;
        private Order order;
        private String cardCompany;
        private String cardNumber;
        private int payPrice;
        private String payStatus;
        private LocalDate createdAt;

        public static PayResponse toDto(Pay pay) {
            return PayResponse.builder()
                    .id(pay.getId())
                    .userId(pay.getUserId())
                    .order(pay.getOrder())
                    .cardCompany(pay.getCardCompany())
                    .cardNumber(pay.getCardNumber())
                    .payPrice(pay.getPayPrice())
                    .payStatus(pay.getPayStatus())
                    .createdAt(pay.getCreatedAt())
                    .build();
        }
}

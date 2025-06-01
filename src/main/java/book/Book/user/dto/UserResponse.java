package book.Book.user.dto;

import book.Book.user.domain.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
public class UserResponse {

    private Long id;
    private String loginId;
    private String username;
    private String email;
    private int mileage;  // 회원 적립급
    private int totalAmount; // 회원 누적 금액
    private String zipcode; // 우편 번호
    private String address; // 주소
    @JsonFormat(pattern = "yyyy-MM")
    private LocalDate createdAt;
    private String role;

    public static UserResponse toDto(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .loginId(user.getLoginId())
                .username(user.getUsername())
                .email(user.getEmail())
                .mileage(user.getMileage())
                .totalAmount(user.getTotalAmount())
                .zipcode(user.getZipcode())
                .address(user.getAddress())
                .createdAt(user.getCreatedAt())
                .role(user.getRole())
                .build();
    }
}

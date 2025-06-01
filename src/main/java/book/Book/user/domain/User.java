package book.Book.user.domain;

import book.Book.user.dto.UserUpdateRequest;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class User {
    private Long id;
    private String loginId;
    private String password;
    private String username;
    private String zipcode; // 우편 번호
    private String address; // 주소
    private String email;
    private int mileage;  // 회원 적립급
    private int totalAmount; // 회원 누적 금액
    private String role;
    private LocalDate createdAt;



    public User edit(UserUpdateRequest request, PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(request.getPassword());
        this.zipcode = request.getZipcode();
        this.address = request.getAddress();
        this.email = request.getEmail();
        return this;
    }




}

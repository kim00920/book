package book.Book.user.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCreateRequest {

    private String loginId;
    private String password;
    private String username;
    private String zipcode; // 우편 번호
    private String address; // 주소
    private String email;

}

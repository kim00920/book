package book.Book.user.service;

import book.Book.user.dto.UserCreateRequest;
import book.Book.user.dto.UserResponse;
import book.Book.user.dto.UserUpdateRequest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

public interface UserService {

    // 회원 가입
    void userSignUp(UserCreateRequest request);

    // 회원 수정
    void userUpdate(UserUpdateRequest request, PasswordEncoder passwordEncoder);

    // 회원 삭제
    void userDelete(Long userId);

    // 회원 탈퇴 (로그인 중일떄)
    void userSignOut();

    // 본인 정보 조회
    UserResponse userInfo();

    // 회원 단건 조회
    UserResponse userFindOne(Long id);

    // 전체 회원 조회
    List<UserResponse> userFindAll();
}

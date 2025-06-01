package book.Book.user.controller;

import book.Book.user.service.UserService;
import book.Book.user.dto.UserResponse;
import book.Book.user.dto.UserUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    // 회원 정보 수정
    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public String updateUser(@RequestBody UserUpdateRequest request) {
        userService.userUpdate(request, passwordEncoder);
        return "회원 정보 수정이 완료됐습니다";
    }

    // 관리자가 회원 삭제
    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    public String deleteUser(@PathVariable("id") Long id) {
        userService.userDelete(id);
        return "회원 정보 삭제가 완료됐습니다";
    }

    // 특정 회원 단건 조회
    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserResponse findOneUser(@PathVariable("id") Long id) {
        return userService.userFindOne(id);
    }

    // 전체 회원 조회
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UserResponse> findAllUsers() {
        return userService.userFindAll();
    }

    // 회원이 자신 정보 조회
    @GetMapping("/myInfo")
    @ResponseStatus(HttpStatus.OK)
    public UserResponse findMyInfo() {
        return userService.userInfo();
    }
}

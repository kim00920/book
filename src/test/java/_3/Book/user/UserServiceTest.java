package _3.Book.user;

import book.Book.error.BusinessException;
import book.Book.mapper.UserMapper;
import book.Book.user.domain.User;
import book.Book.user.dto.UserCreateRequest;
import book.Book.user.dto.UserResponse;
import book.Book.user.dto.UserUpdateRequest;
import book.Book.user.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;

import static book.Book.error.ErrorCode.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("회원 단위테스트")
class UserServiceTest {

    @InjectMocks


    private UserServiceImpl userService;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setupAuthentication() {
        Authentication authentication = new UsernamePasswordAuthenticationToken("kim", null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    @DisplayName("회원가입")
    void userSignUpOK() {
        UserCreateRequest request = UserCreateRequest.builder()
                .username("김경민")
                .loginId("kim")
                .email("kim@example.com")
                .password("password")
                .address("천안시")
                .zipcode("12345")
                .build();

        given(userMapper.validateUserCount("kim")).willReturn(0);
        given(passwordEncoder.encode("password")).willReturn("encodedPassword");

        userService.userSignUp(request);

        then(userMapper).should().signupUser(argThat(user ->
                user.getLoginId().equals("kim") &&
                        user.getPassword().equals("encodedPassword") &&
                        user.getUsername().equals("김경민") &&
                        user.getEmail().equals("kim@example.com")
        ));
    }

    @Test
    @DisplayName("회원가입 실패 (로그인 ID 중복)")
    void userSignUpFail() {
        UserCreateRequest request = UserCreateRequest.builder()
                .loginId("kim")
                .build();

        given(userMapper.validateUserCount("kim")).willReturn(1);

        assertThatThrownBy(() -> userService.userSignUp(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(DUPLICATED_LOGIN_ID.getMessage());
    }

    @Test
    @DisplayName("회원 단건 조회")
    void userFindOneOK() {
        User user = User.builder()
                .id(1L)
                .loginId("kim")
                .username("김경민")
                .email("kim@example.com")
                .build();

        given(userMapper.findById(1L)).willReturn(user);

        UserResponse response = userService.userFindOne(1L);

        assertThat(response.getLoginId()).isEqualTo("kim");
        assertThat(response.getUsername()).isEqualTo("김경민");
    }

    @Test
    @DisplayName("전체 회원 조회")
    void userFindAllOK() {
        User user1 = User.builder().id(1L).loginId("kim").username("김경민").build();
        User user2 = User.builder().id(2L).loginId("kim1").username("김철수").build();

        given(userMapper.findAll()).willReturn(List.of(user1, user2));

        List<UserResponse> userList = userService.userFindAll();

        assertThat(userList).hasSize(2);
        assertThat(userList.get(0).getLoginId()).isEqualTo("kim");
        assertThat(userList.get(1).getLoginId()).isEqualTo("kim1");
    }

    @Test
    @DisplayName("회원 삭제")
    void userDeleteOK() {
        Long userId = 1L;

        willDoNothing().given(userMapper).deleteUser(userId);

        userService.userDelete(userId);

        then(userMapper).should().deleteUser(userId);
    }



    @Test
    @DisplayName("회원 정보 수정")
    void userUpdateOK() {

        User user = User.builder()
                .id(1L)
                .loginId("kim")
                .username("김경민")
                .address("천안시 서북구")
                .zipcode("12345")
                .password("pwd1")
                .build();

        UserUpdateRequest updateRequest = UserUpdateRequest.builder()
                .email("kim@example.com")
                .address("천안시 동남구")
                .zipcode("54321")
                .password("pwd2")
                .build();

        given(userMapper.findByLoginId(anyString())).willReturn(user);
        given(passwordEncoder.encode("pwd2")).willReturn("pwd2");

        userService.userUpdate(updateRequest, passwordEncoder);

        then(userMapper).should().updateUser(user);
        assertThat(user.getUsername()).isEqualTo("김경민");
        assertThat(user.getEmail()).isEqualTo("kim@example.com");
        assertThat(user.getAddress()).isEqualTo("천안시 동남구");
        assertThat(user.getPassword()).isEqualTo("pwd2");
    }
}

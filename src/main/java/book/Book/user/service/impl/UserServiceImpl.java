package book.Book.user.service.impl;

import book.Book.error.BusinessException;
import book.Book.mapper.UserMapper;
import book.Book.user.dto.UserCreateRequest;
import book.Book.user.domain.User;
import book.Book.user.dto.UserResponse;
import book.Book.user.dto.UserUpdateRequest;
import book.Book.user.domain.CustomUserDetails;
import book.Book.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static book.Book.error.ErrorCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    // 회원 가입
    @Override
    public void userSignUp(UserCreateRequest request) {
        log.info("회원가입 요청: loginId={}, email={}", request.getLoginId(), request.getEmail());

        int cnt = userMapper.validateUserCount(request.getLoginId());
        if (cnt > 0) {
            log.warn("회원가입 실패: loginId 중복됨={}", request.getLoginId());
            throw new BusinessException(DUPLICATED_LOGIN_ID);
        }

        String encodePwd = passwordEncoder.encode(request.getPassword());

        User user = User.builder()
                .username(request.getUsername())
                .loginId(request.getLoginId())
                .email(request.getEmail())
                .address(request.getAddress())
                .mileage(0)
                .totalAmount(0)
                .zipcode(request.getZipcode())
                .password(encodePwd)
                .role("BASIC")
                .createdAt(LocalDate.now())
                .build();

        userMapper.signupUser(user);
        log.info("회원가입 성공: loginId={}", user.getLoginId());
    }

    // 회원 수정
    @Override
    public void userUpdate(UserUpdateRequest request, PasswordEncoder passwordEncoder) {
        User user = getUser();
        log.info("회원정보 수정 요청: loginId={}", user.getLoginId());

        user.edit(request, passwordEncoder);
        userMapper.updateUser(user);
        log.info("회원정보 수정 완료: loginId={}", user.getLoginId());
    }

    // 회원 삭제
    @Override
    public void userDelete(Long id) {
        log.info("회원 삭제 요청: userId={}", id);

        userMapper.deleteUser(id);
        log.info("회원 삭제 완료: userId={}", id);
    }

    // 회원 탈퇴 요청
    @Override
    public void userSignOut() {
        User user = getUser();
        log.info("회원 삭제 요청: userId={}", user.getId());
        userMapper.deleteUser(user.getId());
        log.info("회원 삭제 완료: userId={}", user.getId());

    }

    // 본인 정보 조회
    @Override
    @Transactional(readOnly = true)
    public UserResponse userInfo() {
        User user = getUser();
        log.info("본인 정보 조회: loginId={}", user.getLoginId());

        return UserResponse.toDto(user);
    }

    // 회원 1명 조회
    @Override
    @Transactional(readOnly = true)
    public UserResponse userFindOne(Long id) {
        log.info("회원 단건 조회 요청: userId={}", id);

        User user = userMapper.findById(id);
        return UserResponse.toDto(user);
    }

    // 회원 전체 조회
    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> userFindAll() {
        log.info("전체 회원 조회 요청");

        List<User> userList = userMapper.findAll();

        log.info("전체 회원 조회 완료: 총 {}명", userList.size());
        return userList.stream()
                .map(UserResponse::toDto)
                .toList();
    }

    // 현재 로그인 중인 회원 조회
    private User getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loginId = authentication.getName();

        log.debug("인증된 사용자 loginId={}", loginId);
        User user =  userMapper.findByLoginId(loginId);

        if (user == null) {
            log.error("로그인된 사용자 정보 없음: loginId={}", loginId);
            throw new BusinessException(NOT_FOUND_USER);
        }

        return user;
    }

    // 로그인 처리
    @Override
    public UserDetails loadUserByUsername(String loginId) {
        log.info("로그인 시도: loginId={}", loginId);

        User user = Optional.ofNullable(userMapper.findByLoginId(loginId))
                .orElseThrow(() -> {
                    log.warn("로그인 실패 - 사용자 없음: loginId={}", loginId);
                    return new UsernameNotFoundException("해당 사용자를 찾을 수 없습니다: " + loginId);
                });

        log.info("로그인 성공: loginId={}", user.getLoginId());

        return new CustomUserDetails(user, new ArrayList<>());
    }
}

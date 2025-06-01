package book.Book.user.controller;

import book.Book.user.dto.UserCreateRequest;
import book.Book.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/signin")
    public String login(@RequestParam String loginId, @RequestParam String password, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(loginId, password);
        Authentication authentication = authenticationManager.authenticate(authToken);

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        // 세션 저장
        HttpSession session = request.getSession(true);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);

        return "로그인 성공";
    }

    // 회원가입
    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.OK)
    public String signup(@RequestBody UserCreateRequest request) {
        userService.userSignUp(request);
        return "회원가입이 완료됐습니다.";
    }

    // 회원탈퇴
    @DeleteMapping("/signout")
    @ResponseStatus(HttpStatus.OK)
    public String signOut(HttpServletRequest request, HttpServletResponse response) {
        userService.userSignOut();

        new SecurityContextLogoutHandler()
                .logout(request, response, SecurityContextHolder.getContext().getAuthentication());
        return "회원 탈퇴가 완료됐습니다.";

    }
}

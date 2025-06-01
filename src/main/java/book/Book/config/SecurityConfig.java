package book.Book.config;

import book.Book.user.service.impl.UserServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    private final UserServiceImpl UserService;

    public SecurityConfig(@Lazy UserServiceImpl UserService) { // 안붙이니까 순환 의존성이 발생함
        this.UserService = UserService;
    }

    @Bean
    public WebSecurityCustomizer configure() {
        return (web) -> web.ignoring() // 보안 설정에서 특정 요청 경로를 무시
                .requestMatchers((new AntPathRequestMatcher("/mysql-api/**"))) // MySQL API 경로 무시
                .requestMatchers(new AntPathRequestMatcher("/static/**")); // 정적 파일 경로 무시
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(auth -> auth
                        // 인증 없이 접근 가능한 경로
                        .requestMatchers(
                                new AntPathRequestMatcher("/api/signup"),
                                new AntPathRequestMatcher("/api/login")
                        ).permitAll()
                        // 회원가입 API POST 요청은 인증 제외
                        .requestMatchers(HttpMethod.POST, "/api/signup").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/login").permitAll()

                        // 나머지 요청은 인증 필요
                        .anyRequest().authenticated()
                )
                // CSRF 비활성화
                .csrf(AbstractHttpConfigurer::disable)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http, BCryptPasswordEncoder bCryptPasswordEncoder) throws Exception {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(); // 사용자 인증을 위한 제공자
        authProvider.setUserDetailsService(UserService); // 사용자 정보 서비스 설정
        authProvider.setPasswordEncoder(bCryptPasswordEncoder);
        return new ProviderManager(authProvider);
    }

    @Bean
    // 비밀번호 암호화
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
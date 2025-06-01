package book.Book.user.domain;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.GrantedAuthority;
import java.util.Collection;

public class CustomUserDetails implements UserDetails {

    private final User user;
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(User user, Collection<? extends GrantedAuthority> authorities) {
        this.user = user;
        this.authorities = authorities;
    }

    @Override
    public String getUsername() {
        return user.getLoginId();  // 로그인 ID 반환
    }

    @Override
    public String getPassword() {
        return user.getPassword();  // 사용자 패스워드 반환
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;  // 사용자의 권한 정보 반환
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;  // 계정 만료 여부
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;  // 계정 잠금 여부
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;  // 자격 증명 만료 여부
    }

    @Override
    public boolean isEnabled() {
        return true;  // 계정 활성화 여부
    }

    public User user() {
        return this.user;  // User 객체 반환
    }
}

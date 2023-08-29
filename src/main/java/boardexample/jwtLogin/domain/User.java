package boardexample.jwtLogin.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "users")
@ToString()
public class User extends BaseTimeEntity implements UserDetails{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_idx", updatable = false)
    private Long id;


    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password")
    private String password;

    //OAuth 에서 사용할 사용자 이름
    @Column(name = "nickname", nullable = true)
    private String nickname;

    @Column(name = "provider")
    private String provider;

    @Builder
    public User(String email, String password, String auth, String nickname, String provider) {

        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.provider = provider;
    }

    //권한 반환
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        return List.of(new SimpleGrantedAuthority("user"));
    }
    //사용자의 pw를 반환
    @Override
    public String getPassword() {
        return password;
    }

    //사용자의 id를 반환
    @Override
    public String getUsername() {
        return email;
    }

    //계정 만료 여부 반환
    @Override
    public boolean isAccountNonExpired() {
        return true; //true -> 만료되지 않았음
    }

    //계정 잠금 여부 반환
    @Override
    public boolean isAccountNonLocked() {
        return true; //true -> 잠금되지 않았음
    }

    //패스워드 만료 여부 반환
    @Override
    public boolean isCredentialsNonExpired() {
        return true;  //true -> 만료되지 않았음
    }

    //계정 사용 가능 여부 반환
    @Override
    public boolean isEnabled() {
        return true;  //true -> 사용 가능
    }

    //사용자 이름 변경
    public User update(String nickname) {
        this.nickname = nickname;
        return this;
    }
}

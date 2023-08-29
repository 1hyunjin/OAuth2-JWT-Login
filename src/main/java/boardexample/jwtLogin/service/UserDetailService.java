package boardexample.jwtLogin.service;

import boardexample.jwtLogin.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Log4j2
public class UserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    //사용자 email(username)로 사용자의 정보를 가져오기
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        log.info("loadUserByUsername: " + email);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException(email));
    }
}

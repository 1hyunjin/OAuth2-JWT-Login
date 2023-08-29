package boardexample.jwtLogin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class JwtLoginApplication {

	public static void main(String[] args) {
		SpringApplication.run(JwtLoginApplication.class, args);
	}

}

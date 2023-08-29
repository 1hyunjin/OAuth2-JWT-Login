package boardexample.jwtLogin.repository;

import boardexample.jwtLogin.domain.Article;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlogRepository extends JpaRepository<Article, Long> {
}

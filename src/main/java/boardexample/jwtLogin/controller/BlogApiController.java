package boardexample.jwtLogin.controller;

import boardexample.jwtLogin.domain.Article;
import boardexample.jwtLogin.dto.ArticleRequest;
import boardexample.jwtLogin.dto.ArticleResponse;
import boardexample.jwtLogin.service.BlogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController  //HTTP Response Body 객체에 데이터를 JSON 형식으로 변환하는 컨트롤러
@RequiredArgsConstructor
@Log4j2
public class BlogApiController {

    private final BlogService blogService;

    @PostMapping("/api/articles")
    //@RequestBody로 요청 본문 값 매핑
    public ResponseEntity<Article> addArticle(@RequestBody ArticleRequest request, Principal principal) {
        Article savedArticle = blogService.save(request, principal.getName());

        //요청한 자원이 성공정으로 생성되었으며 저장된 블로그 글 정보를 응답 객체에 담아 전송
        return ResponseEntity.status(HttpStatus.CREATED).body(savedArticle);
    }

    @GetMapping("/api/articles")
    public ResponseEntity<List<ArticleResponse>> findAllArticles() {
        List<ArticleResponse> articles = blogService.findAll()
                .stream().map(ArticleResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(articles);
    }

    @GetMapping("/api/articles/{id}")
    public ResponseEntity<ArticleResponse> findArticle(@PathVariable Long id) {
        Article article = blogService.findById(id);

        return ResponseEntity.ok().body(new ArticleResponse(article));
    }

    @DeleteMapping("/api/articles/{id}")
    public ResponseEntity<Void> deleteArticle(@PathVariable Long id) {
        blogService.delete(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/api/articles/{id}")
    public ResponseEntity<Article> updateArticle(@PathVariable Long id,
                                                 @RequestBody ArticleRequest request) {
        Article updatedArticle = blogService.update(id, request);
        return ResponseEntity.ok().body(updatedArticle);
    }

    @GetMapping("/posts/{id}/content")
    public String getPostContentById(@PathVariable Long id) {
        String content = blogService.getPostContentById(id);
        log.info(content);
        return content;
    }
}

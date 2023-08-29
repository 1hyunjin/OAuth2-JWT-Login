package boardexample.jwtLogin.dto;

import boardexample.jwtLogin.domain.Article;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ArticleRequest {
    private String title;
    private String content;

    //DTO -> Entity로 변환
    public Article toEntity(String author) {
        return Article.builder()
                .title(title)
                .content(content)
                .author(author)
                .build();
    }
}

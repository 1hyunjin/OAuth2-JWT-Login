package boardexample.jwtLogin.config.oauth.info;

public interface OAuth2UserInfo {

    String getProvider();

    String getEmail();

    String getName();
}

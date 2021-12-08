package eu.europeana.api.enrichmentsmigration.service;

import eu.europeana.api.enrichmentsmigration.config.AppConfig;
import eu.europeana.api.enrichmentsmigration.model.TokenResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class AuthRestService {

  private final WebClient webClient;
  private static final Logger logger = LogManager.getLogger(EntityManagementRestService.class);

  private volatile String accessToken;
  private final String clientSecret;
  private final String clientId;
  private final String scope;
  private String refreshToken;

  public AuthRestService(AppConfig config) {
    clientId = config.getClientId();
    clientSecret = config.getClientSecret();
    scope = config.getScope();
    refreshToken = config.getRefreshToken();

    this.webClient =
        WebClient.builder()
            .baseUrl(config.getAuthBaseUrl())
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
            .build();
  }

  public void getNewAccessToken() {
    TokenResponse tokenResponse =
        webClient
            .post()
            .uri("/auth/realms/europeana/protocol/openid-connect/token")
            .body(BodyInserters.fromValue(createTokenRefreshBody()))
            .retrieve()
            .bodyToMono(TokenResponse.class)
            .block();

    assert tokenResponse != null;
    accessToken = tokenResponse.getAccessToken();
    refreshToken = tokenResponse.getRefreshToken();
  }

  private String createTokenRefreshBody() {
    return String.format(
        "client_id=%s&"
            + "scope=%s&"
            + "grant_type=refresh_token&"
            + "refresh_token=%s"
            + "&client_secret=%s",
        clientId, scope, refreshToken, clientSecret);
  }

  public String getAccessToken() {
    return accessToken;
  }
}

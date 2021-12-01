package eu.europeana.api.enrichmentsmigration.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TokenResponse {

  @JsonProperty("refresh_token")
  private String refreshToken;

  @JsonProperty("access_token")
  private String accessToken;

  public String getRefreshToken() {
    return refreshToken;
  }

  public String getAccessToken() {
    return accessToken;
  }
}

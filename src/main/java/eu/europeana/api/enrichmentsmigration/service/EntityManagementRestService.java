package eu.europeana.api.enrichmentsmigration.service;

import eu.europeana.api.enrichmentsmigration.config.AppConfig;
import eu.europeana.api.enrichmentsmigration.exception.ServiceException;
import eu.europeana.api.enrichmentsmigration.model.EntityRequestBody;
import eu.europeana.api.enrichmentsmigration.model.IsShownBy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
public class EntityManagementRestService {
  private final WebClient webClient;
  private static final Logger logger = LogManager.getLogger(EntityManagementRestService.class);

  private volatile boolean isTokenRefreshed = false;
  private final AuthRestService authService;

  public EntityManagementRestService(AppConfig appConfig, AuthRestService authService) {
    this.authService = authService;
    this.webClient =
        WebClient.builder()
            .baseUrl(appConfig.getEntityManagementUrl())
            .defaultHeaders(
                header -> header.setBearerAuth(appConfig.getEntityManagementRequestToken()))
            //            .filter(WebClientFilter.logRequest())
            //            .filter(WebClientFilter.logResponse())

            .build();
  }

  public void registerEntity(String entityId, String externalId) {
    String requestPath = getEntityRequestPath(entityId);
    logger.info("Making POST request requestPath={}; externalId={}", requestPath, entityId);
    try {
      webClient
          .post()
          .uri("/entity/" + requestPath + "/management")
          .accept(MediaType.APPLICATION_JSON)
          .contentType(MediaType.APPLICATION_JSON)
          .body(BodyInserters.fromValue(new EntityRequestBody(externalId)))
          .retrieve()
          .toBodilessEntity()
          .block();
    } catch (WebClientResponseException we) {
      logger.error(
          "Error from POST request entityId={}; statusCode={}; response={}",
          entityId,
          we.getRawStatusCode(),
          we.getResponseBodyAsString());
      throw new ServiceException(we.getMessage(), we.getRawStatusCode());
    }
  }

  /** Gets the "{type}/{identifier}" from an EntityId string */
  private String getEntityRequestPath(String entityId) {
    // entity id is "http://data.europeana.eu/{type}/base/{identifier}"
    String idWithoutNamespace = entityId.replace("/base/", "/");
    String[] parts = idWithoutNamespace.split("/");

    String entityType = parts[parts.length - 2];

    // capitalize first character of entity type

    // base not included in request path
    return StringUtils.capitalize(entityType) + "/" + parts[parts.length - 1];
  }

  public void updateIsShownBy(IsShownBy item) {
    try {
      webClient
          .put()
          .uri("/entity/" + getEntityRequestPath(item.getEntityId()))
          .headers(header -> header.setBearerAuth(authService.getAccessToken()))
          .accept(MediaType.APPLICATION_JSON)
          .contentType(MediaType.APPLICATION_JSON)
          .body(BodyInserters.fromValue(createRequestBody(item)))
          .retrieve()
          .toBodilessEntity()
          .block();
      logger.info("Submitted isShownBy for {}", item.getEntityId());
    } catch (WebClientResponseException we) {
      logger.error(
          "Error from POST request entityId={}; statusCode={}; response={}",
          item.getEntityId(),
          we.getRawStatusCode(),
          we.getResponseBodyAsString());

      if (we.getRawStatusCode() == 401) {
        // prevent multiple threads from simultaneously refreshing token
        if(!isTokenRefreshed){
          synchronized (this) {
            if (!isTokenRefreshed) {
              logger.info("Obtaining new token");
              authService.getNewAccessToken();
              logger.info("Token obtained. Making new request");
              isTokenRefreshed = true;
            }
          }
          updateIsShownBy(item);
          isTokenRefreshed = false;
        }
      }
    }
  }

  private static String createRequestBody(IsShownBy item) {
    return String.format(
        "{"
            + "\"@context\": \"http://www.europeana.eu/schemas/context/entity.jsonld\", "
            + "\"type\": \"%s\","
            + "\"isShownBy\": {"
            + "\"id\": \"%s\","
            + "\"source\": \"%s\","
            + "\"thumbnail\": \"%s\""
            + "}"
            + "}",
        getEntityType(item.getEntityId()),
        item.getIsShownById(),
        item.getIsShownBySource(),
        item.getIsShownByThumbnail());
  }

  private static String getEntityType(String entityId) {
    String[] parts = entityId.split("/");
    return StringUtils.capitalize(parts[parts.length - 2]);
  }
}

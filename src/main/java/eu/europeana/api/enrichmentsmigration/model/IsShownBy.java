package eu.europeana.api.enrichmentsmigration.model;

public class IsShownBy {

  private final String entityId;
  private final String isShownById;
  private final String isShownBySource;
  private final String isShownByThumbnail;

  public IsShownBy(
      String entityId, String isShownById, String isShownBySource, String isShownByThumbnail) {
    this.entityId = entityId;
    this.isShownById = isShownById;
    this.isShownBySource = isShownBySource;
    this.isShownByThumbnail = isShownByThumbnail;
  }

  public String getEntityId() {
    return entityId;
  }

  public String getIsShownById() {
    return isShownById;
  }

  public String getIsShownBySource() {
    return isShownBySource;
  }

  public String getIsShownByThumbnail() {
    return isShownByThumbnail;
  }
}

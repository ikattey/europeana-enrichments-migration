package eu.europeana.api.enrichmentsmigration.model;

public class EnrichmentEntity {
  private String externalId;
  private String entityId;

  public EnrichmentEntity(String externalId, String entityId) {
    this.externalId = externalId;
    this.entityId = entityId;
  }

  public String getEntityId() {
    return entityId;
  }

  public void setEntityId(String entityId) {
    this.entityId = entityId;
  }

  public String getExternalId() {
    return externalId;
  }

  public void setExternalId(String externalId) {
    this.externalId = externalId;
  }
}

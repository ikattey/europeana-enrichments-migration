package eu.europeana.api.enrichmentsmigration.model;

import java.util.List;

public class EnrichmentEntity {
  private String entityId;
  private String externalId;
  private List<String> sameAsValues;

  public String getEntityId() {
    return entityId;
  }

  public void setEntityId(String entityId) {
    this.entityId = entityId;
  }

  public List<String> getSameAsValues() {
    return sameAsValues;
  }

  public void setSameAsValues(List<String> sameAsValues) {
    this.sameAsValues = sameAsValues;
  }

  public String getExternalId() {
    return externalId;
  }

  public void setExternalId(String externalId) {
    this.externalId = externalId;
  }
}

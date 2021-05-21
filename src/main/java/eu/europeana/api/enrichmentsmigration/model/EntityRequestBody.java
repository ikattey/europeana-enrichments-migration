package eu.europeana.api.enrichmentsmigration.model;

public class EntityRequestBody {
  private final String id;

  public EntityRequestBody(String id) {
    this.id = id;
  }

  public String getId() {
    return id;
  }
}

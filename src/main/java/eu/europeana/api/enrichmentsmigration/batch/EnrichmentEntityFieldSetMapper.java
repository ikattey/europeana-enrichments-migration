package eu.europeana.api.enrichmentsmigration.batch;

import static eu.europeana.api.enrichmentsmigration.batch.BatchConstants.ENTITY_ID;
import static eu.europeana.api.enrichmentsmigration.batch.BatchConstants.EXTERNAL_ID;

import eu.europeana.api.enrichmentsmigration.model.EnrichmentEntity;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.boot.context.properties.bind.BindException;

public class EnrichmentEntityFieldSetMapper implements FieldSetMapper<EnrichmentEntity> {

  @Override
  public EnrichmentEntity mapFieldSet(FieldSet fieldSet) throws BindException {
    String entityId = fieldSet.readString(ENTITY_ID);
    String externalId = fieldSet.readString(EXTERNAL_ID);

    return new EnrichmentEntity(externalId, entityId);
  }
}

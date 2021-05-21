package eu.europeana.api.enrichmentsmigration.batch;

import eu.europeana.api.enrichmentsmigration.model.EnrichmentEntity;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.boot.context.properties.bind.BindException;

public class EnrichmentEntityFieldSetMapper implements FieldSetMapper<EnrichmentEntity> {

  @Override
  public EnrichmentEntity mapFieldSet(FieldSet fieldSet) throws BindException {
    EnrichmentEntity enrichmentEntity = new EnrichmentEntity();
    enrichmentEntity.setEntityId(fieldSet.readString("enrichmentEntity.about"));

    String owlSameAs = fieldSet.readString("enrichmentEntity.owlSameAs");

    // owlSameAs values are enclosed in square brackets: ie. ["http://abc,"http://dfc]
    enrichmentEntity.setSameAsValues(
        Arrays.asList(owlSameAs.substring(1, owlSameAs.length() - 1).split("\\s*,\\s*")));
    return enrichmentEntity;
  }
}

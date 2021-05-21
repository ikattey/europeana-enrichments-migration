package eu.europeana.api.enrichmentsmigration.batch;

import eu.europeana.api.enrichmentsmigration.model.EnrichmentEntity;
import eu.europeana.api.enrichmentsmigration.service.EntityManagementRestService;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EntityManagementWriter implements ItemWriter<EnrichmentEntity> {

  private static final Logger logger = LogManager.getLogger(EntityManagementWriter.class);

  private final EntityManagementRestService restService;

  @Autowired
  public EntityManagementWriter(EntityManagementRestService restService) {
    this.restService = restService;
  }

  @Override
  public void write(List<? extends EnrichmentEntity> list) throws Exception {
    for (EnrichmentEntity entity : list) {
      if (entity.getExternalId() == null) {
        logger.warn("No externalId for entityId={}", entity.getEntityId());
        // something went wrong during processing
        throw new RuntimeException(
            String.format("No externalId for entityId %s", entity.getEntityId()));
      }
      restService.registerEntity(entity.getEntityId(), entity.getExternalId());
      logger.info(
          "Registered entityId={}, externalId={}", entity.getEntityId(), entity.getExternalId());
    }
  }
}

package eu.europeana.api.enrichmentsmigration.batch;

import eu.europeana.api.enrichmentsmigration.exception.ServiceException;
import eu.europeana.api.enrichmentsmigration.model.EnrichmentEntity;
import eu.europeana.api.enrichmentsmigration.service.CountService;
import java.util.Arrays;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.listener.ItemListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EntityMigrationListener
    extends ItemListenerSupport<EnrichmentEntity, EnrichmentEntity> {

  private final CountService counter;

  private static final Logger logger = LogManager.getLogger(EntityMigrationListener.class);

  @Autowired
  public EntityMigrationListener(CountService counter) {
    this.counter = counter;
  }

  @Override
  public void afterWrite(List<? extends EnrichmentEntity> entities) {
    if (entities.isEmpty()) {
      return;
    }
    long totalSuccessCount = counter.addSuccess(entities.size());
    logger.info(
        "afterWrite: Successfully registered entities entityId={}; totalSuccessCount={}",
        Arrays.toString(getEntityIds(entities)),
        totalSuccessCount);
  }

  public static String[] getEntityIds(List<? extends EnrichmentEntity> records) {
    return records.stream().map(EnrichmentEntity::getEntityId).toArray(String[]::new);
  }
}

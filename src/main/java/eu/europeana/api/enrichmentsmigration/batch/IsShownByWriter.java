package eu.europeana.api.enrichmentsmigration.batch;

import eu.europeana.api.enrichmentsmigration.model.IsShownBy;
import eu.europeana.api.enrichmentsmigration.service.EntityManagementRestService;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Component
public class IsShownByWriter implements ItemWriter<IsShownBy> {

  private static final Logger logger = LogManager.getLogger(EntityManagementWriter.class);

  private final EntityManagementRestService restService;

  public IsShownByWriter(
      EntityManagementRestService restService) {
    this.restService = restService;
  }


  @Override
  public void write(List<? extends IsShownBy> list) throws Exception {
    for(IsShownBy item : list){
      restService.updateIsShownBy(item);
    }
  }
}

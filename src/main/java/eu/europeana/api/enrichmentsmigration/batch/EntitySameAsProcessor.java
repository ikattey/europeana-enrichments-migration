package eu.europeana.api.enrichmentsmigration.batch;

import eu.europeana.api.enrichmentsmigration.model.EnrichmentEntity;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class EntitySameAsProcessor implements ItemProcessor<EnrichmentEntity, EnrichmentEntity> {
  static final String GEO_NAMES_HTTP = "http://sws.geonames.org/";
  static final String GEO_NAME_HTTPS = "https://sws.geonames.org/";

  private static final List<String> dataSources =
      List.of(
          "http://www.wikidata.org/",
          "http://vocab.getty.edu/aat/",
          "http://vocab.getty.edu/ulan/",
          "http://viaf.org/viaf/",
          GEO_NAMES_HTTP,
          "http://www.mimo-db.eu/",
          "http://thesaurus.europeanafashion.eu/thesaurus/");

  private static final Logger logger = LogManager.getLogger(EntitySameAsProcessor.class);

  @Override
  public EnrichmentEntity process(EnrichmentEntity enrichmentEntity) throws Exception {
    List<String> sameAsValues = enrichmentEntity.getSameAsValues();

    for (String sourceUrl : dataSources) {
      String match = getMatchingSource(sameAsValues, sourceUrl);
      if (match != null) {
        // EntityManagement expects https:// for GeoNames
        if (match.equals(GEO_NAMES_HTTP)) {
          match = GEO_NAME_HTTPS;
        }

        enrichmentEntity.setExternalId(match);
        return enrichmentEntity;
      }
    }

    logger.debug(
        "No matching datasource for entityId={}, sameAsValues={}",
        enrichmentEntity.getEntityId(),
        sameAsValues);
    // stop processing
    return null;
  }

  private String getMatchingSource(List<String> sameAs, String sourceUrl) {
    for (String id : sameAs) {
      if (id.contains(sourceUrl)) {
        return id;
      }
    }

    return null;
  }
}

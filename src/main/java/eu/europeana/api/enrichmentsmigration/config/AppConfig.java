package eu.europeana.api.enrichmentsmigration.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@Configuration
@PropertySources({ @PropertySource("classpath:enrichments-migration.properties"),
    @PropertySource(value = "classpath:enrichments-migration.user.properties", ignoreResourceNotFound = true) })
public class AppConfig {
  @Value("${entity-management.url}")
  private String entityManagementUrl;

  @Value("${entity-management-migration-token}")
  private String entityManagementRequestToken;

  @Value("${entities-csv.directory}")
  private String entitiesCsvDirectory;

  @Value("${executor.corePool: 10}")
  private int corePoolSize;

  @Value("${executor.maxPool: 20}")
  private int maxPoolSize;

  @Value("${executor.queueSize: 20}")
  private int queueCapacity;

  @Value("${entities-csv.skipLimit: 30}")
  private int skipLimit;

  public String getEntityManagementUrl() {
    return entityManagementUrl;
  }

  public String getEntitiesCsvDirectory() {
    return entitiesCsvDirectory;
  }

  public int getCorePoolSize() {
    return corePoolSize;
  }

  public int getMaxPoolSize() {
    return maxPoolSize;
  }

  public int getQueueCapacity() {
    return queueCapacity;
  }

  public int getSkipLimit() {
    return skipLimit;
  }

  public String getEntityManagementRequestToken() {
    return entityManagementRequestToken;
  }
}

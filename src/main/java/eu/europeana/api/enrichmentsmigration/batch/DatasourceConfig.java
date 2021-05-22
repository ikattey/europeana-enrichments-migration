package eu.europeana.api.enrichmentsmigration.batch;

import java.net.MalformedURLException;
import javax.sql.DataSource;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@PropertySources({
  @PropertySource("classpath:enrichments-migration.properties"),
  @PropertySource(
      value = "classpath:enrichments-migration.user.properties",
      ignoreResourceNotFound = true)
})
public class DatasourceConfig {

  @Autowired private DataSource dataSource;

  @Value("classpath:org/springframework/batch/core/schema-drop-postgresql.sql")
  private Resource dropRepositoryTables;

  @Value("classpath:org/springframework/batch/core/schema-postgresql.sql")
  private Resource dataRepositorySchema;

  @Bean
  public DataSourceInitializer dataSourceInitializer(DataSource dataSource)
      throws MalformedURLException {
    ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator();

    databasePopulator.addScript(dropRepositoryTables);
    databasePopulator.addScript(dataRepositorySchema);
    databasePopulator.setIgnoreFailedDrops(true);

    DataSourceInitializer initializer = new DataSourceInitializer();
    initializer.setDataSource(dataSource);
    initializer.setDatabasePopulator(databasePopulator);

    return initializer;
  }

  private JobRepository getJobRepository() throws Exception {
    JobRepositoryFactoryBean factory = new JobRepositoryFactoryBean();
    factory.setDataSource(dataSource);
    factory.setTransactionManager(getTransactionManager());
    factory.afterPropertiesSet();
    return factory.getObject();
  }

  private PlatformTransactionManager getTransactionManager() {
    return new ResourcelessTransactionManager();
  }

  public JobLauncher getJobLauncher() throws Exception {
    SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
    jobLauncher.setJobRepository(getJobRepository());
    jobLauncher.afterPropertiesSet();
    return jobLauncher;
  }
}

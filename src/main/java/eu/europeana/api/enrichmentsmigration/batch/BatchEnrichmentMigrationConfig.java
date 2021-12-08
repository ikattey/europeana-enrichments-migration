package eu.europeana.api.enrichmentsmigration.batch;

import static eu.europeana.api.enrichmentsmigration.batch.BatchConstants.ENTITY_ID;
import static eu.europeana.api.enrichmentsmigration.batch.BatchConstants.IS_SHOWN_BY_ID;
import static eu.europeana.api.enrichmentsmigration.batch.BatchConstants.IS_SHOWN_BY_SOURCE;
import static eu.europeana.api.enrichmentsmigration.batch.BatchConstants.IS_SHOWN_BY_THUMBNAIL;

import eu.europeana.api.enrichmentsmigration.config.AppConfig;
import eu.europeana.api.enrichmentsmigration.model.IsShownBy;
import java.io.IOException;
import java.net.MalformedURLException;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

@SuppressWarnings("SpringConfigurationProxyMethods")
@Component
public class BatchEnrichmentMigrationConfig {
  final ResourcePatternResolver resourcePatternResolver;

  private final JobBuilderFactory jobs;

  private final StepBuilderFactory steps;

  private final FlatFileItemReader<IsShownBy> reader;

  private final IsShownByWriter writer;
  private final EntityMigrationListener listener;

  private final AppConfig appConfig;

  @Autowired
  public BatchEnrichmentMigrationConfig(
      ResourcePatternResolver resourcePatternResolver,
      JobBuilderFactory jobs,
      StepBuilderFactory steps,
      FlatFileItemReader<IsShownBy> reader,
      IsShownByWriter writer,
      EntityMigrationListener listener,
      AppConfig appConfig) {
    this.resourcePatternResolver = resourcePatternResolver;
    this.jobs = jobs;
    this.steps = steps;
    this.reader = reader;
    this.writer = writer;
    this.listener = listener;
    this.appConfig = appConfig;
  }

  @Bean
  public Job partitionerJob()
      throws UnexpectedInputException, MalformedURLException, ParseException {
    return jobs.get("partitionerJob").start(partitionStep()).build();
  }

  @Bean
  public Step partitionStep() throws UnexpectedInputException, ParseException {
    return steps
        .get("partitionStep")
        .partitioner("slaveStep", partitioner())
        .step(slaveStep())
        .taskExecutor(taskExecutor())
        .build();
  }

  @Bean
  public Step slaveStep() throws UnexpectedInputException, ParseException {
    return steps
        .get("slaveStep")
        .<IsShownBy, IsShownBy>chunk(50)
        .reader(reader)
        .faultTolerant()
        // do not fail on errors
        .skipPolicy((Throwable t, int skipCount) -> true)
        .writer(writer)
        .build();
  }

  @Bean
  public CustomMultiResourcePartitioner partitioner() {
    CustomMultiResourcePartitioner partitioner = new CustomMultiResourcePartitioner();
    Resource[] resources;
    try {
      resources =
          resourcePatternResolver.getResources(
              "file:" + appConfig.getEntitiesCsvDirectory() + "*.csv");
    } catch (IOException e) {
      throw new RuntimeException("I/O problems when resolving the input file pattern.", e);
    }
    partitioner.setResources(resources);
    return partitioner;
  }

  @Bean
  @StepScope
  public FlatFileItemReader<IsShownBy> itemReader(
      @Value("#{stepExecutionContext[fileName]}") String filename)
      throws UnexpectedInputException, ParseException {
    FlatFileItemReader<IsShownBy> reader = new FlatFileItemReader<>();
    DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
    String[] tokens = {ENTITY_ID, IS_SHOWN_BY_SOURCE, IS_SHOWN_BY_ID, IS_SHOWN_BY_THUMBNAIL};
    tokenizer.setNames(tokens);
    reader.setResource(new FileSystemResource(appConfig.getEntitiesCsvDirectory() + filename));
    DefaultLineMapper<IsShownBy> lineMapper = new DefaultLineMapper<>();
    lineMapper.setLineTokenizer(tokenizer);
    lineMapper.setFieldSetMapper(
        (FieldSet fieldSet) ->
            new IsShownBy(
                fieldSet.readString(ENTITY_ID),
                fieldSet.readString(IS_SHOWN_BY_SOURCE),
                fieldSet.readString(IS_SHOWN_BY_ID),
                fieldSet.readString(IS_SHOWN_BY_THUMBNAIL)));
    reader.setLineMapper(lineMapper);
    return reader;
  }

  @Bean
  public TaskExecutor taskExecutor() {
    ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
    taskExecutor.setMaxPoolSize(appConfig.getMaxPoolSize());
    taskExecutor.setCorePoolSize(appConfig.getCorePoolSize());
    taskExecutor.setQueueCapacity(appConfig.getQueueCapacity());
    taskExecutor.afterPropertiesSet();
    return taskExecutor;
  }
}

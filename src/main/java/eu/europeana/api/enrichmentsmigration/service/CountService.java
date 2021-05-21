package eu.europeana.api.enrichmentsmigration.service;

import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Service;

@Service
public class CountService {
  private final AtomicLong successCount = new AtomicLong(0);
  private final AtomicLong failureCount = new AtomicLong(0);

  public long addSuccess(long count) {
    return successCount.addAndGet(count);
  }

  public long addFailure(long count) {
    return failureCount.addAndGet(count);
  }
}

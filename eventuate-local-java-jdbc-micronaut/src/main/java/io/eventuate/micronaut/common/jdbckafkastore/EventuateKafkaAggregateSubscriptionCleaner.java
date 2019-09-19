package io.eventuate.micronaut.common.jdbckafkastore;

import io.micronaut.context.annotation.Context;

import javax.annotation.PreDestroy;
import java.util.Arrays;

@Context
public class EventuateKafkaAggregateSubscriptionCleaner {
  private EventuateKafkaAggregateSubscriptionCleaner[] eventuateKafkaAggregateSubscriptionCleaners;

  @PreDestroy
  public void clean() {
    Arrays.stream(eventuateKafkaAggregateSubscriptionCleaners).forEach(EventuateKafkaAggregateSubscriptionCleaner::clean);
  }

}

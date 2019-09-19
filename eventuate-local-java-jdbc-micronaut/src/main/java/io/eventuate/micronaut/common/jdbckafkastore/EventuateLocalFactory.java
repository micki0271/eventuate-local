package io.eventuate.micronaut.common.jdbckafkastore;

import io.eventuate.common.jdbc.EventuateCommonJdbcOperations;
import io.eventuate.common.jdbckafkastore.EventuateKafkaAggregateSubscriptions;
import io.eventuate.common.jdbckafkastore.EventuateLocalAggregateCrud;
import io.eventuate.common.jdbckafkastore.EventuateLocalJdbcAccess;
import io.eventuate.javaclient.commonimpl.AggregateCrud;
import io.eventuate.javaclient.commonimpl.AggregateEvents;
import io.eventuate.javaclient.commonimpl.adapters.AsyncToSyncAggregateEventsAdapter;
import io.eventuate.javaclient.commonimpl.adapters.AsyncToSyncTimeoutOptions;
import io.eventuate.javaclient.commonimpl.adapters.SyncToAsyncAggregateCrudAdapter;
import io.eventuate.javaclient.spring.jdbc.EventuateJdbcAccess;
import io.eventuate.common.jdbc.EventuateSchema;
import io.eventuate.messaging.kafka.basic.consumer.EventuateKafkaConsumerConfigurationProperties;
import io.eventuate.messaging.kafka.common.EventuateKafkaConfigurationProperties;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Requires;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Nullable;
import javax.inject.Singleton;
import javax.sql.DataSource;

@Factory
public class EventuateLocalFactory {

  @Singleton
  public EventuateJdbcAccess eventuateJdbcAccess(TransactionTemplate transactionTemplate,
                                                 EventuateSchema eventuateSchema,
                                                 EventuateCommonJdbcOperations eventuateCommonJdbcOperations,
                                                 JdbcTemplate jdbcTemplate) {
    return new EventuateLocalJdbcAccess(transactionTemplate, jdbcTemplate, eventuateCommonJdbcOperations, eventuateSchema);
  }

  @Singleton
  public EventuateLocalAggregateCrud eventuateLocalAggregateCrud(TransactionTemplate transactionTemplate,
                                                                 EventuateJdbcAccess eventuateJdbcAccess) {
    return new EventuateLocalAggregateCrud(transactionTemplate, eventuateJdbcAccess);
  }

  @Singleton
  public AggregateCrud asyncAggregateCrud(io.eventuate.javaclient.commonimpl.sync.AggregateCrud aggregateCrud) {
    return new SyncToAsyncAggregateCrudAdapter(aggregateCrud);
  }


  // Events

  @Singleton
  public EventuateKafkaAggregateSubscriptions aggregateEvents(EventuateKafkaConfigurationProperties eventuateLocalAggregateStoreConfiguration,
                                                              EventuateKafkaConsumerConfigurationProperties eventuateKafkaConsumerConfigurationProperties) {
    return new EventuateKafkaAggregateSubscriptions(eventuateLocalAggregateStoreConfiguration, eventuateKafkaConsumerConfigurationProperties);
  }


  @Singleton
  public io.eventuate.javaclient.commonimpl.sync.AggregateEvents syncAggregateEvents(@Nullable AsyncToSyncTimeoutOptions timeoutOptions,
                                                                                     AggregateEvents aggregateEvents) {
    AsyncToSyncAggregateEventsAdapter adapter = new AsyncToSyncAggregateEventsAdapter(aggregateEvents);
    if (timeoutOptions != null)
      adapter.setTimeoutOptions(timeoutOptions);
    return adapter;
  }

  @Singleton
  public TransactionTemplate transactionTemplate(PlatformTransactionManager platformTransactionManager) {
    return new TransactionTemplate(platformTransactionManager);
  }

  @Singleton
  @Requires(missingBeans = PlatformTransactionManager.class)
  public PlatformTransactionManager platformTransactionManager(DataSource dataSource) {
    return new DataSourceTransactionManager(dataSource);
  }

  // Aggregate Store
  // Why @ConditionalOnMissingBean(EventuateAggregateStore.class)??
}

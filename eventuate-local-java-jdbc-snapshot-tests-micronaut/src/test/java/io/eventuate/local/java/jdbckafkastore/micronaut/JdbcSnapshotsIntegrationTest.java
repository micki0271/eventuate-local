package io.eventuate.local.java.jdbckafkastore.micronaut;

import io.micronaut.test.annotation.MicronautTest;

@MicronautTest(transactional = false)
public class JdbcSnapshotsIntegrationTest extends AbstractSnapshotsIntegrationTest {

}

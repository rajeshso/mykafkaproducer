package com.n2.kafkaProducer.config;

import io.confluent.kafka.schemaregistry.RestApp;
import org.junit.rules.ExternalResource;

public class SchemaRegistryRule extends ExternalResource {

  private final RestApp schemaRegistry;

  public SchemaRegistryRule(final String zKConnectString, final int port) {
    schemaRegistry = new RestApp(port, zKConnectString, "_schemas");
  }

  protected void before() throws Exception {
    this.schemaRegistry.start();
  }

  protected void after() {
    try {
      this.schemaRegistry.stop();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}

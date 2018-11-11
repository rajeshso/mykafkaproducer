package com.n2.kafkaProducer.config;

import io.confluent.kafka.serializers.KafkaAvroSerializer;
import io.confluent.kafka.serializers.KafkaAvroSerializerConfig;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
public class InterestEventProducerConfig {

  private final String schemaRegistryUrl;
  private final String clientId;

  private final BaseKafkaConfig baseKafkaConfig;

  InterestEventProducerConfig(
      @Value("${kafka.schema.registry.url}") String schemaRegistryUrl,
      @Value("${kafka.subscription-event-producer.clientId}") String clientId,
      BaseKafkaConfig baseKafkaConfig) {
    this.schemaRegistryUrl = schemaRegistryUrl;
    this.clientId = clientId;
    this.baseKafkaConfig = baseKafkaConfig;
  }

  Map<String, Object> producerConfig() {
    Map<String, Object> configProperties = new HashMap<>();
    //default config
    configProperties.putAll(baseKafkaConfig.defaultConfig());

    configProperties.put(ProducerConfig.CLIENT_ID_CONFIG, clientId);
    configProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    configProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
    configProperties.put(KafkaAvroSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG, schemaRegistryUrl);

    return configProperties;
  }

  @Bean
  public KafkaTemplate kafkaTemplate() {
    return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(producerConfig()));
  }
}

package com.n2.kafkaProducer.config;

import static java.util.Collections.singletonList;
import static java.util.UUID.randomUUID;
import static org.apache.kafka.clients.CommonClientConfigs.CLIENT_ID_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.AUTO_OFFSET_RESET_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.GROUP_ID_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG;

import com.n2.event.bar3.MyEvent;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import java.util.Properties;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

public class KafkaService {

  private final String bootstrapServers;
  private final String schemaRegistryUrl;
  private final String clientId;

  private KafkaConsumer kafkaConsumer;
  public KafkaService(final String bootstrapServers, final String clientId,
      final String schemaRegistryUrl) {
    this.bootstrapServers = bootstrapServers;
    this.clientId = clientId;
    this.schemaRegistryUrl = schemaRegistryUrl;
  }

  private Properties consumerProperties() {
    Properties properties = new Properties();
    properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    properties.put(KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
    properties.put(VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class.getName());
    properties.put(GROUP_ID_CONFIG, randomUUID().toString());
    properties.put(AUTO_OFFSET_RESET_CONFIG, "earliest");
    properties.put(CLIENT_ID_CONFIG, clientId + randomUUID());
    properties.put(KafkaAvroDeserializerConfig.SCHEMA_REGISTRY_URL_CONFIG,schemaRegistryUrl);
    properties.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, true);
    return properties;
  }

  public void createConsumer(String topicName) {
    kafkaConsumer = new KafkaConsumer(consumerProperties());
    kafkaConsumer.subscribe(singletonList(topicName));
  }

  public ConsumerRecords<String, MyEvent> consume() {
    return kafkaConsumer.poll(250);
  }

  public void resetOffset() {
    kafkaConsumer.seekToBeginning(kafkaConsumer.assignment());
  }
}

package com.n2.kafkaProducer.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import io.confluent.kafka.serializers.KafkaAvroSerializer;
import io.confluent.kafka.serializers.KafkaAvroSerializerConfig;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class InterestEventProducerConfigTest {

  private static final String SCHEMA_REGISTRY_URL = "TEST_SCHEMA_URL";
  private static final String CLIENT_ID = "TEST_CLIENT_ID";

  private Map<String, Object> defaultConfig = new HashMap<>();

  @Mock
  private BaseKafkaConfig baseKafkaConfig;

  private InterestEventProducerConfig unit;

  @Before
  public void setUp() {
    defaultConfig.put("DEFAULT_KEY", "DEFAULT_VALUE");
  }

  @Test
  public void producerConfig_shouldReturnTheConfig() {
    //given
    unit = new InterestEventProducerConfig(SCHEMA_REGISTRY_URL, CLIENT_ID, baseKafkaConfig);
    when(baseKafkaConfig.defaultConfig()).thenReturn(defaultConfig);
    //when
    Map<String, Object> actual = unit.producerConfig();
    //then
    assertThat(actual).containsAllEntriesOf(getExpectedConfig());
  }

  private Map<String, Object> getExpectedConfig() {
    Map<String, Object> expected = new HashMap<>();
    //default config
    expected.putAll(defaultConfig);

    expected.put(ProducerConfig.CLIENT_ID_CONFIG, CLIENT_ID);
    expected.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    expected.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
    expected.put(KafkaAvroSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG, SCHEMA_REGISTRY_URL);

    return expected;
  }
}
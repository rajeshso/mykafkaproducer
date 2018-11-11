package com.n2.kafkaProducer.config;

import static org.apache.kafka.clients.CommonClientConfigs.REQUEST_TIMEOUT_MS_CONFIG;
import static org.apache.kafka.clients.CommonClientConfigs.RETRY_BACKOFF_MS_CONFIG;
import static org.apache.kafka.clients.CommonClientConfigs.SECURITY_PROTOCOL_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG;
import static org.apache.kafka.common.config.SaslConfigs.SASL_JAAS_CONFIG;
import static org.apache.kafka.common.config.SaslConfigs.SASL_MECHANISM;
import static org.apache.kafka.common.config.SslConfigs.SSL_ENDPOINT_IDENTIFICATION_ALGORITHM_CONFIG;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;
import org.junit.Test;

public class BaseKafkaConfigTest {

  private static final String BOOTSTRAP_SERVERS = "TEST_BOOTSTRAP_SERVER";
  private static final String API_KEY = "TEST_KEY";
  private static final String API_SECRET = "TEST_PASSWORD";
  private static final String SSL_ALGORITHM = "TEST_ALGORITHM";
  private static final String SASL_MECHANISM_VALUE = "TEST_SASL";
  private static final String SECURITY_PROTOCOL = "TEST_PROTOCOL";
  private static final String REQUEST_TIMEOUT_MS = "TEST_REQUEST_TIMEOUT_MS";
  private static final String RETRY_BACKOFF_MS = "TEST_RETRY_BACKOFF";

  private BaseKafkaConfig unit;

  @Test
  public void defaultConfig_shouldReturnDefaultConfig() {
    //given
    unit = new BaseKafkaConfig(BOOTSTRAP_SERVERS, API_KEY, API_SECRET, SSL_ALGORITHM,
        SASL_MECHANISM_VALUE, SECURITY_PROTOCOL, REQUEST_TIMEOUT_MS, RETRY_BACKOFF_MS);
    //when
    Map<String, Object> defaultConfig = unit.defaultConfig();
    //then
    assertThat(defaultConfig).containsAllEntriesOf(getExpectedConfig());
  }

  private Map<String, Object> getExpectedConfig() {
    Map<String, Object> propertiesMap = new HashMap<>();
    //default config
    propertiesMap.put(BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);

    String jaasConfig = String.format(
        "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"%s\" password=\"%s\";",
        API_KEY, API_SECRET);

    propertiesMap.put(SSL_ENDPOINT_IDENTIFICATION_ALGORITHM_CONFIG, SSL_ALGORITHM);
    propertiesMap.put(SASL_MECHANISM, SASL_MECHANISM_VALUE);
    propertiesMap.put(SASL_JAAS_CONFIG, jaasConfig);
    propertiesMap.put(SECURITY_PROTOCOL_CONFIG, SECURITY_PROTOCOL);
    propertiesMap.put(REQUEST_TIMEOUT_MS_CONFIG, REQUEST_TIMEOUT_MS);
    propertiesMap.put(RETRY_BACKOFF_MS_CONFIG, RETRY_BACKOFF_MS);
    return propertiesMap;
  }
}
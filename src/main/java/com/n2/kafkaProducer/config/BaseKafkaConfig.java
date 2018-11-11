package com.n2.kafkaProducer.config;

import static java.lang.String.format;
import static org.apache.kafka.clients.CommonClientConfigs.REQUEST_TIMEOUT_MS_CONFIG;
import static org.apache.kafka.clients.CommonClientConfigs.RETRY_BACKOFF_MS_CONFIG;
import static org.apache.kafka.clients.CommonClientConfigs.SECURITY_PROTOCOL_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG;
import static org.apache.kafka.common.config.SaslConfigs.SASL_JAAS_CONFIG;
import static org.apache.kafka.common.config.SaslConfigs.SASL_MECHANISM;
import static org.apache.kafka.common.config.SslConfigs.SSL_ENDPOINT_IDENTIFICATION_ALGORITHM_CONFIG;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
class BaseKafkaConfig {

  private static final String SASL_JAAS_CONFIG_TEMPLATE = "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"%s\" password=\"%s\";";

  private final String bootstrapServers;
  private final String sslAlgorithm;
  private final String saslMechanism;
  private final String securityProtocol;
  private final String apiKey;
  private final String apiSecret;
  private final String requestTimeout;
  private final String retryBackOff;

  BaseKafkaConfig(@Value("${kafka.bootstrap.servers}") String bootstrapServers,
      @Value("${kafka.sasl.api.key}") String apiKey,
      @Value("${kafka.sasl.api.secret}") String apiSecret,
      @Value("${kafka.ssl.algorithm}") String sslAlgorithm,
      @Value("${kafka.sasl.mechanism}") String saslMechanism,
      @Value("${kafka.security.protocol}") String securityProtocol,
      @Value("${kafka.request.timeout.ms}") String requestTimeout,
      @Value("${kafka.retry.backoff.ms}") String retryBackOff) {

    this.bootstrapServers = bootstrapServers;
    this.apiKey = apiKey;
    this.apiSecret = apiSecret;
    this.sslAlgorithm = sslAlgorithm;
    this.saslMechanism = saslMechanism;
    this.securityProtocol = securityProtocol;
    this.requestTimeout = requestTimeout;
    this.retryBackOff = retryBackOff;
  }

  Map<String, Object> defaultConfig() {

    final Map<String, Object> defaultConfigMap = new HashMap<>();
    final String jaasConfig = format(SASL_JAAS_CONFIG_TEMPLATE, apiKey, apiSecret);

    defaultConfigMap.put(BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    defaultConfigMap.put(SSL_ENDPOINT_IDENTIFICATION_ALGORITHM_CONFIG, sslAlgorithm);
    defaultConfigMap.put(SASL_MECHANISM, saslMechanism);
    defaultConfigMap.put(SASL_JAAS_CONFIG, jaasConfig);
    defaultConfigMap.put(SECURITY_PROTOCOL_CONFIG, securityProtocol);
    defaultConfigMap.put(REQUEST_TIMEOUT_MS_CONFIG, requestTimeout);
    defaultConfigMap.put(RETRY_BACKOFF_MS_CONFIG, retryBackOff);

    return defaultConfigMap;
  }
}

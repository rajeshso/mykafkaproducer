package com.n2.kafkaProducer;


import com.n2.event.bar3.MyEvent;
import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import java.util.Properties;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
// This is a non-spring version of the Producer
public class Producer {

  private static final String TOPIC = "bar3";

  @SuppressWarnings("InfiniteLoopStatement")
  public static void main(final String[] args) {

    final Properties props = new Properties();
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
    props.put(ProducerConfig.ACKS_CONFIG, "all");
    props.put(ProducerConfig.RETRIES_CONFIG, 0);
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
    props.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://localhost:8081");

    try (KafkaProducer<String, MyEvent> producer = new KafkaProducer<String, MyEvent>(props)) {

      for (long i = 0; i < 10; i++) {
        final String orderId = "id" + Long.toString(i);
        final MyEvent payment = new MyEvent(orderId, orderId, (int) i);
        final ProducerRecord<String, MyEvent> record = new ProducerRecord<String, MyEvent>(TOPIC, payment.getMyKey(), payment);
        producer.send(record);
        Thread.sleep(1000L);
      }

    } catch (final InterruptedException e) {
      e.printStackTrace();
    }

    System.out.printf("Successfully produced 10 messages to a topic called %s%n", TOPIC);


  }

}


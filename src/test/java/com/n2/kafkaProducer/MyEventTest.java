package com.n2.kafkaProducer;

import static java.util.UUID.fromString;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.awaitility.Duration.FIVE_SECONDS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.n2.event.bar3.MyEvent;
import com.n2.kafkaProducer.config.KafkaService;
import com.n2.kafkaProducer.config.SchemaRegistryRule;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.streams.integration.utils.EmbeddedKafkaCluster;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.util.concurrent.ListenableFuture;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = {MyKafkaProducer.class})
@ComponentScan(basePackages = {"com.n2.kafka.kafkaProducer.*"})
@ActiveProfiles("local")
@DirtiesContext
public class MyEventTest  {

  private static final String TOPIC = "TEST";
  private static final String MY_KEY = "key";

  @Mock
  private KafkaTemplate kafkaTemplate;

  private KafkaService kafkaService;
  private static final int SCHEMA_REGISTRY_PORT = 9876;
  protected static final String SCHEMA_REGISTRY_URL = "http://localhost:" + SCHEMA_REGISTRY_PORT;

  @InjectMocks
  private MyKafkaProducer unit;

  @ClassRule
  public static EmbeddedKafkaCluster KAFKA_CLUSTER = new EmbeddedKafkaCluster(1,
      brokerProperties());

  @Rule
  public SchemaRegistryRule schemaRegistryRule = new SchemaRegistryRule(
      KAFKA_CLUSTER.zKConnectString(), SCHEMA_REGISTRY_PORT);

  @BeforeClass
  public static void setUpKafkaAndShemaRegistry() throws Exception {
    System.setProperty("kafka.bootstrap.servers", KAFKA_CLUSTER.bootstrapServers());
    System.setProperty("kafka.schema.registry.url", SCHEMA_REGISTRY_URL);
  }

  private static Properties brokerProperties() {
    Properties props = new Properties();
    props.put("transaction.state.log.replication.factor", Short.valueOf("1"));
    props.put("transaction.state.log.min.isr", 1);
    return props;
  }
  @Before
  @BeforeTransaction
  public void setUp() throws URISyntaxException {
    //create kafka service
    kafkaService = new KafkaService(KAFKA_CLUSTER.bootstrapServers(),
        MyEventTest.class.getName(),
        SCHEMA_REGISTRY_URL);
    kafkaService.createConsumer("my-event-v1");
  }

  @Ignore
  public void publishMyEvents_shouldSendEventsToTopic()
      throws ExecutionException, InterruptedException {

    //and event
    MyEvent event = new MyEvent(MY_KEY, "value", 1);

    //and kafka
    ListenableFuture future = mock(ListenableFuture.class);
    when(kafkaTemplate.send(TOPIC, MY_KEY, event)).thenReturn(future);

    //when
    unit.sendMessage(event);

    //then
    verify(kafkaTemplate).send(TOPIC, MY_KEY, event);
    verify(future).get();
  }


  private void verifyNoInteractionsWithKafkaTopic(List<UUID> subscriptionKeys) {
    ConsumerRecords<String, MyEvent> records = kafkaService.consume();
    records.forEach(event -> assertThat(
        subscriptionKeys.contains(fromString(event.value().getMyKey()))).isFalse());
  }

  private void verifyKafkaContainsTheMyEvents(MyEvent myevent,
      String productKey) {
    await().atMost(FIVE_SECONDS).until(() -> {
      ConsumerRecords<String, MyEvent> records = kafkaService.consume();

      if (!records.isEmpty()) {

        for (ConsumerRecord<String, MyEvent> record : records) {
          MyEvent event = record.value();
          if (event.getMyKey().equals(myevent.getMyKey().toString())) {
            assertThat(event.getMyKey())
                .isEqualTo(myevent.getMyKey().toString());
            assertThat(event.getMyValue()).isEqualTo(myevent.getMyValue());
            assertThat(event.getMyVersion()).isEqualTo(myevent.getMyVersion());
            return true;
          }
        }
      }
      return false;
    });
  }
}

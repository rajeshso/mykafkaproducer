package com.n2.kafkaProducer;

import com.n2.event.bar3.MyEvent;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class MyKafkaProducer {

  @Autowired private KafkaTemplate<String, MyEvent> kafkaTemplate;

  @Value("${kafka.topic.boot}")
  private String topicName;

  @PostConstruct
  @Async
  public void sendMessage() {

    try {
      MyEvent myEvent = new MyEvent("Rajesh", "London", 1);
      kafkaTemplate.send(topicName, myEvent.getMyKey() + myEvent.getMyVersion(), myEvent);
      System.out.println(
          "Sent message to kafka topic successfully with subscription Key " + myEvent);

    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Error while sending the subscription event to the topic {} with key {}");
    }
  }
}

package com.n2.kafkaProducer;

import com.n2.event.MyEvent;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Component
public class MyKafkaProducer {

  @Autowired
  private KafkaTemplate<String, MyEvent> kafkaTemplate;

  @Value("${kafka.topic.boot}")
  private String topicName;

  @PostConstruct
  @Async
  public void sendMessage() {

    try {
      MyEvent myEvent = new MyEvent("Rajesh", "161 Belvedere Road", 1);
      ListenableFuture<SendResult<String, MyEvent>> listenableFuture =  kafkaTemplate.send(topicName,
          myEvent.getMyKey()+myEvent.getMyVersion(), myEvent);
      System.out.println("Sent message to kafka topic successfully with subscription Key " +
          myEvent);
      // register a callback with the listener to receive the result of the send asynchronously
      // TODO : The callback may not work properly
      listenableFuture.addCallback(new ListenableFutureCallback<SendResult<String, MyEvent>>() {

        @Override
        public void onSuccess(SendResult<String, MyEvent> result) {
          System.out.println("sent message="+ myEvent+ " with offset={}"+
              result.getRecordMetadata().offset());
        }

        @Override
        public void onFailure(Throwable ex) {
          System.err.println("unable to send message=" + myEvent);
          ex.printStackTrace();
        }
      });
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Error while sending the subscription event to the topic {} with key {}");
    }

  }

}

package com.n2.kafkaProducer;

import java.util.concurrent.ExecutionException;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Component
public class MyKafkaProducer {

  @Autowired
  private KafkaTemplate<String, String> kafkaTemplate;

  @Value("${kafka.topic.boot}")
  private String topicName;

  @PostConstruct
  public void sendMessage() {
    String msg = "hello 123";
    ListenableFuture<SendResult<String, String>> listenableFuture = kafkaTemplate.send(topicName, msg);
    System.out.println("Sent Hello to the topic");
    // register a callback with the listener to receive the result of the send asynchronously
    listenableFuture.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {

      @Override
      public void onSuccess(SendResult<String, String> result) {
        System.out.println("sent message="+ msg+ " with offset={}"+
            result.getRecordMetadata().offset());
      }

      @Override
      public void onFailure(Throwable ex) {
        System.err.println("unable to send message=" + msg);
        ex.printStackTrace();
      }
    });
//    try {
//      while(listenableFuture.isDone()) {
//        SendResult<String, String> sendResult = listenableFuture.get();
//        System.out.println("ProduerRecord is "+ sendResult.getProducerRecord().toString());
//      }
//    } catch (InterruptedException e) {
//      e.printStackTrace();
//    } catch (ExecutionException e) {
//      e.printStackTrace();
//    }
  }

}

package com.n2.kafkaProducer;


import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.n2.event.bar3.MyEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.concurrent.ListenableFuture;

@RunWith(MockitoJUnitRunner.class)
public class InterestEventServiceTest {

  private static final String TOPIC = "TEST";

  @Mock
  private KafkaTemplate kafkaTemplate;

  @InjectMocks
  private MyKafkaProducer unit;

  @Before
  public void setUp() {
    ReflectionTestUtils.setField(unit, "topicName", TOPIC);
  }

  @Test
  public void publishInterestEventsToTopic()
      throws Exception {

    //given
    String partyKey = "aParty";
    String tenantKey = "aMan";

    //and event mocks
    MyEvent event = mock(MyEvent.class);

    //and kafka
    ListenableFuture future = mock(ListenableFuture.class);
    when(kafkaTemplate.send(TOPIC, partyKey + "_" + tenantKey, event)).thenReturn(future);

    //when
    unit.sendMessage(event);

    //then verify if there are any methods are set in the event TODO
    //Also you can check if there are no interactions to publisher for error conditions by using verifyZeroInteractions(kafkaTemplate)
    //verify(event);
  }

}
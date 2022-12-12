package com.pluralsight.practicaljms;

import com.pluralsight.practicaljms.base.AbstractExampleApplication;
import com.pluralsight.practicaljms.base.DelayingMessageListener;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

import javax.jms.*;
import java.util.ArrayList;
import java.util.List;



@SuppressWarnings("Duplicates")
public class Example4 extends AbstractExampleApplication {


    public static void main(String... args) throws JMSException {
        Example4 example = new Example4();
        example.start();
    }

    private List<MessageConsumer> consumers = new ArrayList<>();
    private List<Session> sessions = new ArrayList<>();

    private DefaultMessageListenerContainer container;

    private void start() throws JMSException {
        container = new DefaultMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setConcurrentConsumers(5);
        container.setCacheLevel(DefaultMessageListenerContainer.CACHE_CONSUMER);
        container.setDestinationName("TEST_DESTINATION");
        container.setMessageListener(
                new DelayingMessageListener("Default", 10));
        container.setAutoStartup(true);
        container.initialize();
        container.start();
    }
    protected void sendMessages() throws JMSException {
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Queue queue = session.createQueue("TEST_DESTINATION");
        messageProducer = session.createProducer(queue);
        for (int x=0; x<100; x++) {
            TextMessage textMessage = session.createTextMessage("Message " + x);
            messageProducer.send(textMessage);
        }
    }

    @Override
    public void shutdown() throws JMSException {
        messageProducer.close();
        for (MessageConsumer consumer : consumers) {
            consumer.close();
        }
        for (Session session : sessions) {
            session.close();
        }

        super.shutdown();
    }


}

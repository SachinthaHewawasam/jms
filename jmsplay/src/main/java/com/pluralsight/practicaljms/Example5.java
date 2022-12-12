package com.pluralsight.practicaljms;

import com.pluralsight.practicaljms.base.AbstractExampleApplication;
import com.pluralsight.practicaljms.base.DelayingMessageListener;

import javax.jms.*;
import java.util.ArrayList;
import java.util.List;



@SuppressWarnings("Duplicates")
public class Example5 extends AbstractExampleApplication {


    public static void main(String... args) throws JMSException {
        Example5 example = new Example5();
        example.start();
    }

    private List<MessageConsumer> consumers = new ArrayList<>();
    private List<Session> sessions = new ArrayList<>();


    private void start() throws JMSException {
        for (int x=0; x<5 ; x++) {
            Session session = connection
                    .createSession(false, Session.AUTO_ACKNOWLEDGE);
            Queue queue = session.createQueue("TEST_DESTINATION");
            sessions.add(session);
            MessageConsumer consumer = session.createConsumer(queue);
            if (x % 2 == 0) {
                consumer.setMessageListener(
                        new DelayingMessageListener(
                                String.valueOf(x) + " Fast", 10));
            } else {
                consumer.setMessageListener(
                        new DelayingMessageListener(
                                String.valueOf(x) + " Slow", 100));
            }
            consumers.add(consumer);
        }
        connection.start();
    }

    protected void sendMessages() throws JMSException {
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Queue queue = session.createQueue("TEST_DESTINATION");
        messageProducer = session.createProducer(queue);

        for (int x=0; x<100; x++) {
            TextMessage textMessage = session.createTextMessage("Message " + x);
            if (x % 2 == 0) {
                textMessage.setStringProperty("JMSXGroupID", "Even");
            } else {
                textMessage.setStringProperty("JMSXGroupID", "Odd");
            }
            // Could be used for an account number or an order
            //textMessage.setStringProperty("JMSXGroupID", orderOrAccountNumber);
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

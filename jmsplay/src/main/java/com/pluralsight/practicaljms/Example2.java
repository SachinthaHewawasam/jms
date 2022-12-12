package com.pluralsight.practicaljms;

import com.pluralsight.practicaljms.base.AbstractExampleApplication;
import com.pluralsight.practicaljms.base.DelayingMessageListener;

import javax.jms.*;
import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("Duplicates")
public class Example2 extends AbstractExampleApplication {


    public static void main(String... args) throws JMSException {
        Example2 example = new Example2();
        example.start();
    }

    private List<MessageConsumer> consumers = new ArrayList<>();
    private List<Session> sessions = new ArrayList<>();

    private void start() throws JMSException {
        for (int x=0; x<5 ; x++) {
            Session session = connection
                    .createSession(false, Session.AUTO_ACKNOWLEDGE);
            Queue queue = session
                    .createQueue("TEST_DESTINATION");
            sessions.add(session);
            MessageConsumer consumer = session
                    .createConsumer(queue);
            consumer.setMessageListener(
                    new DelayingMessageListener(
                            String.valueOf(x) + " Consumer", 100));
            consumers.add(consumer);
        }
        connection.start();
    }

    @Override
    public void shutdown() throws JMSException {
        for (MessageConsumer consumer : consumers) {
            consumer.close();
        }
        for (Session session : sessions) {
            session.close();
        }
        super.shutdown();
    }


}

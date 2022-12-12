package com.pluralsight.practicaljms;

import com.pluralsight.practicaljms.base.AbstractExampleApplication;
import com.pluralsight.practicaljms.base.DelayingMessageListener;

import javax.jms.*;


public class Example1 extends AbstractExampleApplication {


    public static void main(String... args) throws Exception {
        Example1 example = new Example1();
        example.start();
    }

    private MessageConsumer messageConsumer;

    private void start() throws JMSException {
        MessageListener messageListener =
                new DelayingMessageListener(
                        "Consumer", 200);
        session = connection
                .createSession(false, Session.AUTO_ACKNOWLEDGE);
        Queue queue = session
                .createQueue("TEST_DESTINATION");
        messageConsumer = session
                .createConsumer(queue);
        messageConsumer
                .setMessageListener(messageListener);
        connection.start();
    }

    @Override
    public void shutdown() throws JMSException {
        messageConsumer.close();
        session.close();
        super.shutdown();
    }

    @Override
    protected void sendMessages() throws JMSException {
        // Do nothing
    }
}

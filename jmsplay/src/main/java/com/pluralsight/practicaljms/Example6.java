package com.pluralsight.practicaljms;

import com.pluralsight.practicaljms.base.AbstractExampleApplication;
import com.pluralsight.practicaljms.base.DelayingMessageListener;

import javax.jms.*;


public class Example6 extends AbstractExampleApplication {


    public static void main(String... args) throws Exception {
        Example6 example = new Example6();
        example.start();
    }

    private MessageConsumer messageConsumer;

    private void start() throws JMSException {
        MessageListener messageListener =
                new DelayingMessageListener("Consumer", 0);
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Queue queue = session.createQueue("TEST_DESTINATION");
        messageConsumer = session.createConsumer(queue);
        messageConsumer.setMessageListener(messageListener);

        connection.setExceptionListener(new MyExceptionListener());

        connection.start();
    }

    private class MyExceptionListener implements ExceptionListener {
        @Override
        public void onException(JMSException e) {
            System.out.println("Handling Exception : " + e.getMessage());
            boolean connected = false;
            while (!connected) {
                try {
                    shutdown();
                    connect();
                    connected = true;
                    start();
                    System.out.println("Reconnected");
                } catch (JMSException ex) {
                    connected = false;
                    try {
                        System.out.println("Retrying connect in 2 seconds");
                        Thread.sleep(2000L);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void shutdown() throws JMSException {
        messageConsumer.close();
        session.close();
        super.shutdown();
    }


}

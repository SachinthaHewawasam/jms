package com.pluralsight.practicaljms;

import com.pluralsight.practicaljms.base.AbstractExampleApplication;

import javax.jms.*;


@SuppressWarnings("Duplicates")
public class Example9 extends AbstractExampleApplication {


    public static void main(String... args) throws Exception {
        Example9 example = new Example9();
        example.start();
    }

    private MessageConsumer messageConsumer;
    private Session sendSession;
    private Session receiveSession;

    private void start() throws JMSException {
        receiveSession = connection
                .createSession(false, Session.AUTO_ACKNOWLEDGE);
        Queue queue = receiveSession.createQueue("TEST_DESTINATION");
        messageConsumer = receiveSession.createConsumer(queue);

        messageConsumer.setMessageListener(messageListener -> {
            //Throw an Exception to replicate something going wrong
            throw new RuntimeException("TEST");
        });

        connection.start();
    }



    @Override
    protected void sendMessages() throws JMSException {
        sendSession = connection
                .createSession(false, Session.AUTO_ACKNOWLEDGE);
        messageProducer = sendSession
                .createProducer(session.createQueue("TEST_DESTINATION"));
        TextMessage requestMessage = sendSession.createTextMessage("Message!");

        messageProducer.send(requestMessage);
    }

    @Override
    public void shutdown() throws JMSException {
        messageConsumer.close();
        receiveSession.close();
        sendSession.close();
        super.shutdown();
    }


}

package com.pluralsight.practicaljms;

import com.pluralsight.practicaljms.base.AbstractExampleApplication;
import com.pluralsight.practicaljms.base.CorrelationIdReplyMessageListener;

import javax.jms.*;


@SuppressWarnings("Duplicates")
public class Example8 extends AbstractExampleApplication {


    public static void main(String... args) throws Exception {
        Example8 example = new Example8();
        example.start();
    }

    private MessageConsumer messageConsumer;
    private CorrelationIdReplyMessageListener messageListener;
    private Session sendSession;
    private Session receiveSession;

    private void start() throws JMSException {

        startConsumerService();

        //Send a message and blocking waiting for a response
        sendSession = connection
                .createSession(false, Session.AUTO_ACKNOWLEDGE);
        messageProducer = sendSession
                .createProducer(session.createQueue("REQUEST_QUEUE"));
        TextMessage requestMessage = sendSession.createTextMessage("Request");
        System.out.println("Sending request : " + requestMessage.getText());
        messageProducer.send(requestMessage);

        TextMessage responseMessage = blockForMessage(requestMessage);
        System.out.println("Reply was : " + responseMessage.getText());
        shutdown();
    }

    private TextMessage blockForMessage(Message requestMessage) throws JMSException {
        Queue replyQueue = session.createQueue("REPLY_QUEUE");
        MessageConsumer consumer = session.createConsumer(replyQueue,
                "JMSCorrelationID = '"
                        + requestMessage.getJMSMessageID() + "'");
        Message responseMessage = consumer.receive();
        consumer.close();
        return (TextMessage)responseMessage;
    }

    /**
     * Starts a consumer, that models another component or service the consumes from the queue.
     */
    private void startConsumerService() throws JMSException{
        receiveSession = connection
                .createSession(false, Session.AUTO_ACKNOWLEDGE);
        Queue queue = receiveSession.createQueue("REQUEST_QUEUE");
        messageConsumer = receiveSession.createConsumer(queue);

        messageListener = new CorrelationIdReplyMessageListener(connection);
        messageConsumer.setMessageListener(messageListener);

        connection.start();
    }

    @Override
    protected void sendMessages() throws JMSException {
        //Do nothing here
    }

    @Override
    public void shutdown() throws JMSException {
        messageListener.shutdown();
        messageConsumer.close();
        receiveSession.close();
        sendSession.close();
        super.shutdown();
    }


}

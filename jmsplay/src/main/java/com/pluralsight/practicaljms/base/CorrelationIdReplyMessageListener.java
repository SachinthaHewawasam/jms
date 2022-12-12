package com.pluralsight.practicaljms.base;

import javax.jms.*;


public class CorrelationIdReplyMessageListener implements MessageListener {

    private Session session = null;
    private MessageProducer producer = null;
    private Queue replyQueue = null;

    public CorrelationIdReplyMessageListener(Connection connection) throws JMSException {
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        replyQueue = session.createQueue("REPLY_QUEUE");
        producer = session.createProducer(replyQueue);
    }

    public void shutdown() throws JMSException {
        //We're using session caching, so close the session
        producer.close();
        session.close();
    }


    @Override
    public void onMessage(Message message) {

        try {
            TextMessage requestMessage = (TextMessage)message;
            TextMessage reply = session
                    .createTextMessage(
                            "Response to message " + requestMessage.getText()
                    );

            reply.setJMSCorrelationID(message.getJMSMessageID());

            producer.send(replyQueue, reply);
        } catch (JMSException e) {
            e.printStackTrace();

        } finally {
            if (null != producer) {
                try {
                    producer.close();
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}

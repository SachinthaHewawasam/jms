package com.pluralsight.practicaljms.base;

import javax.jms.*;


public class ReplyToQueueMessageListener implements MessageListener {

    private Session session;

    public ReplyToQueueMessageListener(Connection connection) throws JMSException {
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
    }

    public void shutdown() throws JMSException {
        //We're using session caching, so close the session
        session.close();
    }


    @Override
    public void onMessage(Message message) {
        MessageProducer producer = null;
        try {

            TextMessage textMessage = (TextMessage)message;
            Destination replyToDestination = textMessage.getJMSReplyTo();
            producer = session.createProducer(replyToDestination);
            TextMessage reply = session.createTextMessage("Response to message " + textMessage.getText());
            producer.send(reply);


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

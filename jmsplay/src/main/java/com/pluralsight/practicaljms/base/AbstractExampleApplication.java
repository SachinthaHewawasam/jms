package com.pluralsight.practicaljms.base;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQPrefetchPolicy;
import org.apache.activemq.RedeliveryPolicy;

import javax.jms.*;


public abstract class AbstractExampleApplication {
    public AbstractExampleApplication() {
        try {
            connect();
            sendMessages();
        } catch (JMSException e) {
            e.printStackTrace();
            System.exit(1);
        }
        addShutdownHook();
    }


    protected ConnectionFactory connectionFactory;
    protected Connection connection;
    protected Session session;
    protected MessageProducer messageProducer;
    private Queue queue;


    protected void connect() throws JMSException {
        connectionFactory = createConnectionFactory();
        connection = connectionFactory.createConnection();
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        queue = session.createQueue("TEST_DESTINATION");
        messageProducer = session.createProducer(queue);

    }



    protected void sendMessages() throws JMSException {

        for (int x=0; x<100; x++) {
            messageProducer.send(session.createTextMessage("Message " + x));
        }
    }

    protected void shutdown() throws JMSException {
        if (null != messageProducer) {
            messageProducer.close();
        }
        connection.close();
        session.close();
    }

    protected ConnectionFactory createConnectionFactory() {
        ActiveMQConnectionFactory connectionFactory
                = new ActiveMQConnectionFactory("tcp://localhost:61616");

        ActiveMQPrefetchPolicy prefetchPolicy = new ActiveMQPrefetchPolicy();
        prefetchPolicy.setQueuePrefetch(1);

        RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();
        redeliveryPolicy.setMaximumRedeliveries(4);
        redeliveryPolicy.setBackOffMultiplier(2);
        redeliveryPolicy.setInitialRedeliveryDelay(200);
        redeliveryPolicy.setUseExponentialBackOff(true);
        connectionFactory.setRedeliveryPolicy(redeliveryPolicy);

        connectionFactory.setPrefetchPolicy(prefetchPolicy);

        return connectionFactory;
    }

    void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    shutdown();
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        });
    }


}

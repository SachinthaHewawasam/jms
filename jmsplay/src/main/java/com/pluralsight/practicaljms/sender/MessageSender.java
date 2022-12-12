package com.pluralsight.practicaljms.sender;

import com.pluralsight.practicaljms.base.AbstractExampleApplication;

public class MessageSender extends AbstractExampleApplication {

    public static void main(String... args) throws Exception {
        //Sending of 100 messages is performed as part of the constructor
        MessageSender application = new MessageSender();
        application.shutdown();
    }




}

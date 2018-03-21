package com.myprojects.expense.tracker.service;

import com.google.protobuf.MessageLite;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.AbstractMessageConverter;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.stereotype.Service;

@Service
public class EventToMessageConverter extends AbstractMessageConverter {

    @Override
    protected Message createMessage(Object message, MessageProperties messageProperties) {
        if (message instanceof MessageLite) {
            byte[] messageBody = ((MessageLite) message).toByteArray();
            return new Message(messageBody, messageProperties);
        }
        throw new IllegalArgumentException("message is not a protobuf one!");
    }

    @Override
    public Object fromMessage(Message message) throws MessageConversionException {
        throw new UnsupportedOperationException();
    }

}

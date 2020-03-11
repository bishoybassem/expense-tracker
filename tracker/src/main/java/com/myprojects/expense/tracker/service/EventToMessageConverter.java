package com.myprojects.expense.tracker.service;

import com.google.protobuf.MessageLite;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.AbstractMessageConverter;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.stereotype.Service;

/**
 * Maps between MQ messages and transaction events.
 */
@Service
public class EventToMessageConverter extends AbstractMessageConverter {

    /**
     * Converts the given protobuf transaction event to a MQ message.
     *
     * If the given event is not a protobuf one, it throws an {@link IllegalArgumentException}.
     */
    @Override
    protected Message createMessage(Object event, MessageProperties messageProperties) {
        if (event instanceof MessageLite) {
            byte[] messageBody = ((MessageLite) event).toByteArray();
            return new Message(messageBody, messageProperties);
        }
        throw new IllegalArgumentException("message is not a protobuf one!");
    }

    @Override
    public Object fromMessage(Message message) throws MessageConversionException {
        throw new UnsupportedOperationException();
    }

}

package com.myprojects.expense.reporter.service;

import com.google.protobuf.InvalidProtocolBufferException;
import com.myprojects.expense.messages.EventProtos;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.AbstractMessageConverter;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.stereotype.Service;

@Service
public class MessageToEventConverter extends AbstractMessageConverter {

    @Override
    protected Message createMessage(Object object, MessageProperties messageProperties) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object fromMessage(Message message) throws MessageConversionException {
        try {
            return EventProtos.Event.parseFrom(message.getBody());
        } catch (InvalidProtocolBufferException e) {
            throw new MessageConversionException("could not convert message body to event", e);
        }
    }

}

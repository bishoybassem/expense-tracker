package com.myprojects.expense.reporter.service;

import com.myprojects.expense.messages.EventProtos.Event;
import com.myprojects.expense.messages.EventProtos.EventType;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.testng.annotations.Test;

import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.text.IsEmptyString.isEmptyString;

public class MessageToEventConverterTests {

    private static final MessageToEventConverter CONVERTER = new MessageToEventConverter();

    @Test
    public void testFromMessage() throws Exception {
        Message message = new Message(Event.newBuilder()
                .setType(EventType.CREATE)
                .setTransactionId(UUID.randomUUID().toString())
                .build()
                .toByteArray(), null);

        Event convertedEvent = (Event) CONVERTER.fromMessage(message);
        assertThat(convertedEvent.getType(), is(EventType.CREATE));
        assertThat(convertedEvent.getTransactionId(), not(isEmptyString()));
    }

    @Test(expectedExceptions = MessageConversionException.class)
    public void testFromMessageWithInvalidMessage() throws Exception {
        CONVERTER.fromMessage(new Message(new byte[]{1, 2, 3}, null));
    }

    @Test(expectedExceptions = UnsupportedOperationException.class)
    public void testCreateMessageDoesNothing() throws Exception {
        CONVERTER.createMessage(null, null);
    }
}
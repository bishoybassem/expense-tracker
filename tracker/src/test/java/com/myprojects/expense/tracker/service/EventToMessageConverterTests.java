package com.myprojects.expense.tracker.service;


import com.myprojects.expense.messages.EventProtos.Event;
import com.myprojects.expense.messages.EventProtos.EventType;
import org.springframework.amqp.core.Message;
import org.testng.annotations.Test;

import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyString;

public class EventToMessageConverterTests {

    private static final EventToMessageConverter CONVERTER = new EventToMessageConverter();

    @Test
    public void testCreateMessage() throws Exception {
        Message message = CONVERTER.createMessage(Event.newBuilder()
                .setType(EventType.DELETE)
                .setTransactionId(UUID.randomUUID().toString())
                .build(), null);

        Event parsedEvent = Event.parseFrom(message.getBody());
        assertThat(parsedEvent.getType(), is(EventType.DELETE));
        assertThat(parsedEvent.getTransactionId(), not(emptyString()));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testCreateMessageFromNonProtobufOne() throws Exception {
        CONVERTER.createMessage("abc", null);
    }

    @Test(expectedExceptions = UnsupportedOperationException.class)
    public void testFromMessageThrowsException() throws Exception {
        CONVERTER.fromMessage(null);
    }
}

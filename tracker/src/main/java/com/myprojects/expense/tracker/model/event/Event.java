package com.myprojects.expense.tracker.model.event;

import java.util.UUID;

public class Event {

    private EventType eventType;
    private UUID transactionId;
    private EventData transactionData;

    protected Event(EventType eventType, UUID transactionId) {
        this.eventType = eventType;
        this.transactionId = transactionId;
    }

    public EventType getEventType() {
        return eventType;
    }

    public UUID getTransactionId() {
        return transactionId;
    }

    public EventData getTransactionData() {
        return transactionData;
    }

    public void setTransactionData(EventData transactionData) {
        this.transactionData = transactionData;
    }
}

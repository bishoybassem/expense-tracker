package com.myprojects.expense.tracker.model.event;

import java.util.UUID;

public class ModifyEvent extends Event {

    private EventData newTransactionData;

    public ModifyEvent(UUID transactionId) {
        super(EventType.MODIFY, transactionId);
    }

    public EventData getNewTransactionData() {
        return newTransactionData;
    }

    public void setNewTransactionData(EventData newTransactionData) {
        this.newTransactionData = newTransactionData;
    }
}

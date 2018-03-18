package com.myprojects.expense.tracker.model.event;

import java.util.UUID;

public class DeleteEvent extends Event {

    public DeleteEvent(UUID transactionId) {
        super(EventType.DELETE, transactionId);
    }

}

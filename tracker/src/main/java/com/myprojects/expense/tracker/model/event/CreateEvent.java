package com.myprojects.expense.tracker.model.event;

import java.util.UUID;

public class CreateEvent extends Event{

    public CreateEvent(UUID transactionId) {
        super(EventType.CREATE, transactionId);
    }

}

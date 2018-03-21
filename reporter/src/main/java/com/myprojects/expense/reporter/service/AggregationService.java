package com.myprojects.expense.reporter.service;

import com.myprojects.expense.messages.EventProtos.Event;

public interface AggregationService {

    void handleTransactionEvent(Event event);

}

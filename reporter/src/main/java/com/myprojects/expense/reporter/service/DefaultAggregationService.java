package com.myprojects.expense.reporter.service;

import com.myprojects.expense.messages.EventProtos.Event;
import org.springframework.stereotype.Service;

@Service
public class DefaultAggregationService implements AggregationService {

    @Override
    public void handleTransactionEvent(Event event) {
        System.out.println(event.getTransactionId());
    }
    
}

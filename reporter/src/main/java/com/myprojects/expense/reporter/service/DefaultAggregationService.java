package com.myprojects.expense.reporter.service;

import org.apache.commons.codec.Charsets;
import org.springframework.stereotype.Service;

@Service
public class DefaultAggregationService implements AggregationService {

    @Override
    public void handleMessage(byte[] message) {
        System.out.println("Got message: " + new String(message, Charsets.UTF_8));
    }
    
}

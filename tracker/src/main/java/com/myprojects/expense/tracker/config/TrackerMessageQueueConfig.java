package com.myprojects.expense.tracker.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TrackerMessageQueueConfig {

    private static final String EXCHANGE_NAME = "expense-events";

    @Bean
    public Exchange exchange() {
        return new FanoutExchange(EXCHANGE_NAME);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, ObjectMapper objectMapper) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setExchange(EXCHANGE_NAME);
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter(objectMapper));
        return rabbitTemplate;
    }

}

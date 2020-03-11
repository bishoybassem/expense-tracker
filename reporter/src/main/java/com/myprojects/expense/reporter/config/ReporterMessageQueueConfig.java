package com.myprojects.expense.reporter.config;

import com.myprojects.expense.reporter.service.MessageToEventConverter;
import com.myprojects.expense.reporter.service.TransactionEventHandler;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ReporterMessageQueueConfig {

    private static final String EXCHANGE_NAME = "expense-events";
    private static final String QUEUE_NAME = "reporter-queue";

    @Bean
    public FanoutExchange fanoutExchange() {
        return new FanoutExchange(EXCHANGE_NAME);
    }

    @Bean
    public Queue queue() {
        return new Queue(QUEUE_NAME);
    }

    @Bean
    public Binding binding(Queue queue, FanoutExchange fanoutExchange) {
        return BindingBuilder.bind(queue).to(fanoutExchange);
    }

    @Bean
    public SimpleMessageListenerContainer messageListenerContainer(ConnectionFactory connectionFactory,
                                                                   MessageToEventConverter converter,
                                                                   TransactionEventHandler eventHandler) {
        MessageListenerAdapter listener = new MessageListenerAdapter(eventHandler);
        listener.setDefaultListenerMethod("handleTransactionEvent");
        listener.setMessageConverter(converter);

        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(QUEUE_NAME);
        container.setMessageListener(listener);
        return container;
    }

}

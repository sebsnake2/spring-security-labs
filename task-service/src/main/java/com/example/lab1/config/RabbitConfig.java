package com.example.lab1.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String TASK_EXCHANGE = "task.events.exchange";
    public static final String TASK_QUEUE = "task.events.scheduler";
    public static final String TASK_ROUTING_KEY = "task.events";

    @Bean
    public DirectExchange taskExchange() {
        return new DirectExchange(TASK_EXCHANGE, true, false);
    }

    @Bean
    public Queue taskQueue() {
        return new Queue(TASK_QUEUE, true);
    }

    @Bean
    public Binding taskBinding(Queue taskQueue, DirectExchange taskExchange) {
        return BindingBuilder.bind(taskQueue)
                .to(taskExchange)
                .with(TASK_ROUTING_KEY);
    }
}
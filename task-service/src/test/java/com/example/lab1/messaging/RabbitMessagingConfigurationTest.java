package com.example.lab1.messaging;

import com.example.lab1.config.RabbitConfig;
import com.example.lab1.event.TaskEventPublisher;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

class RabbitMessagingConfigurationTest {

    private final RabbitConfig rabbitConfig = new RabbitConfig();

    @Test
    @DisplayName("Task exchange is durable")
    void exchangeIsDurable() {
        DirectExchange exchange = rabbitConfig.taskExchange();

        assertEquals(RabbitConfig.TASK_EXCHANGE, exchange.getName());
        assertTrue(exchange.isDurable());
        assertFalse(exchange.isAutoDelete());
    }

    @Test
    @DisplayName("Task queue is durable")
    void queueIsDurable() {
        Queue queue = rabbitConfig.taskQueue();

        assertEquals(RabbitConfig.TASK_QUEUE, queue.getName());
        assertTrue(queue.isDurable());
        assertFalse(queue.isAutoDelete());
    }

    @Test
    @DisplayName("Queue is bound to task exchange with correct routing key")
    void bindingIsConfiguredCorrectly() {
        Queue queue = rabbitConfig.taskQueue();
        DirectExchange exchange = rabbitConfig.taskExchange();

        Binding binding = rabbitConfig.taskBinding(queue, exchange);

        assertEquals(RabbitConfig.TASK_QUEUE, binding.getDestination());
        assertEquals(RabbitConfig.TASK_EXCHANGE, binding.getExchange());
        assertEquals(RabbitConfig.TASK_ROUTING_KEY, binding.getRoutingKey());
    }

    @Test
    @DisplayName("Rabbit publication happens only after successful transaction commit")
    void publisherUsesAfterCommit() throws NoSuchMethodException {
        Method method = TaskEventPublisher.class
                .getDeclaredMethod("publish", com.example.lab1.event.TaskChangedEvent.class);

        TransactionalEventListener annotation =
                method.getAnnotation(TransactionalEventListener.class);

        assertNotNull(annotation);
        assertEquals(TransactionPhase.AFTER_COMMIT, annotation.phase());
    }
}
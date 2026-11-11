package com.example.lab1.event;

import com.example.lab1.config.RabbitConfig;
import tools.jackson.databind.json.JsonMapper;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class TaskEventPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final JsonMapper jsonMapper;

    public TaskEventPublisher(RabbitTemplate rabbitTemplate, JsonMapper jsonMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.jsonMapper = jsonMapper;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publish(TaskChangedEvent event) {
        String json = jsonMapper.writeValueAsString(event);

        rabbitTemplate.convertAndSend(
                RabbitConfig.TASK_EXCHANGE,
                RabbitConfig.TASK_ROUTING_KEY,
                json,
                message -> {
                    message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                    message.getMessageProperties().setContentType("application/json");
                    return message;
                }
        );
    }
}
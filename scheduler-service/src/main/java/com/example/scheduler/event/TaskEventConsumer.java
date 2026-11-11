package com.example.scheduler.event;

import com.example.scheduler.config.RabbitConfig;
import com.example.scheduler.service.TaskEventProcessingService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

@Component
public class TaskEventConsumer {

    private final JsonMapper jsonMapper;
    private final TaskEventProcessingService processingService;

    public TaskEventConsumer(
            JsonMapper jsonMapper,
            TaskEventProcessingService processingService
    ) {
        this.jsonMapper = jsonMapper;
        this.processingService = processingService;
    }

    @RabbitListener(queues = RabbitConfig.TASK_QUEUE)
    public void consume(String json) {
        TaskChangedMessage event = jsonMapper.readValue(json, TaskChangedMessage.class);
        processingService.process(event);
    }
}
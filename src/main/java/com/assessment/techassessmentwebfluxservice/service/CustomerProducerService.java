package com.assessment.techassessmentwebfluxservice.service;

import com.assessment.techassessmentwebfluxservice.MessageHandler;
import com.assessment.techassessmentwebfluxservice.entity.CustomerEntity;
import com.assessment.techassessmentwebfluxservice.model.CustomerRecord;
import com.techassessment.techassessmentwebfluxservice.model.Customer;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.stereotype.Service;

@Service
@EnableKafka
public class CustomerProducerService {
    private static final Logger log = LoggerFactory.getLogger(CustomerProducerService.class);
    private final MessageHandler<String, CustomerRecord> messageHandler;

    @Value("${spring.kafka.tech-assessment.customer-topic.topic-name}")
    private String topicName;

    public CustomerProducerService(MessageHandler<String, CustomerRecord> messageHandler) {
        this.messageHandler = messageHandler;
    }

    public void publish(Customer payload) {
        log.info("Publishing customer: {}", payload);

        messageHandler.publish(
                UUID.randomUUID().toString(),
                topicName,
                payload.getCustomerId(),
                CustomerEntity.toRecord(payload)
        );
    }
}

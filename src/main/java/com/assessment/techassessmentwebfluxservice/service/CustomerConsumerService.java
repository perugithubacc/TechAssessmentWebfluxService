package com.assessment.techassessmentwebfluxservice.service;

import com.assessment.techassessmentwebfluxservice.entity.CustomerEntity;
import com.assessment.techassessmentwebfluxservice.model.CustomerRecord;
import com.assessment.techassessmentwebfluxservice.repository.CustomerRepository;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

@Service
public class CustomerConsumerService {

    private static final Logger log = LoggerFactory.getLogger(CustomerServiceImpl.class);

    private final CustomerRepository customerRepository;

    @Autowired
    public CustomerConsumerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @KafkaListener(
            topics = "${spring.kafka.tech-assessment.customer-topic.topic-name}",
            groupId = "${spring.kafka.tech-assessment.customer-topic.group-id}",
            concurrency = "${spring.kafka.tech-assessment.customer-topic.concurrency}"
    )
    public void consumeCustomerRecord(
            @Header("message-handler-key") String messageKey,
            ConsumerRecord<String, CustomerRecord> record
    ) {
        log.info("Received customer message key {}", messageKey);
        customerRepository.save(CustomerEntity.toEntity(record.value()))
                .doOnSuccess(savedEntity -> {
                    Long customerId = savedEntity.getCustomerId();
                    log.info("Customer info saved for customerId: {}", customerId);
                })
                .doOnError(e -> {
                    log.error("Error occurred while saving customer data: {}", e.getMessage());
                    throw new RuntimeException(e.getMessage());
                })
                .subscribe();
    }
}

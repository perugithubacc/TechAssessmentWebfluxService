package com.assessment.techassessmentwebfluxservice;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class MessageHandler<K,V> {

    private static final Logger log = LoggerFactory.getLogger(MessageHandler.class);
    private final KafkaTemplate<K, V> kafkaTemplate;
    private final KafkaAdmin kafkaAdmin;

    public MessageHandler(KafkaTemplate<K, V> kafkaTemplate, KafkaAdmin kafkaAdmin) {
        this.kafkaTemplate = kafkaTemplate;
        this.kafkaAdmin = kafkaAdmin;
    }

    public void publish(String messageKey, String topic, K key, V payload) {
        log.info("Message key: {}, topic: {}, key: {}, message: {}", messageKey, topic, key, payload);
        createTopicIfNotExists(topic);
        ProducerRecord<K, V> record = new ProducerRecord<>(topic, key, payload);
        record.headers().add("message-handler-key", messageKey.getBytes());
        log.info("Published data for message key: {}", messageKey);
        kafkaTemplate.send(record);
    }

    private void createTopicIfNotExists(String topic) {
        NewTopic newTopic = TopicBuilder
                .name(topic)
                .partitions(1)
                .replicas(1)
                .build();
        kafkaAdmin.createOrModifyTopics(newTopic);
    }
}

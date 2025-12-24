package com.recipeadjuster.shared.kafka;

import com.recipeadjuster.shared.config.KafkaTopics;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.*;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig
@EmbeddedKafka(
    partitions = 2,
    topics = {
        KafkaTopics.INGREDIENT_SUBMITTED,
        KafkaTopics.RECIPE_MATCHED,
        KafkaTopics.SUBSTITUTION_REQUESTED,
        KafkaTopics.USER_PREFERENCE_UPDATED,
        KafkaTopics.ANALYTICS_EVENT
    }
)
class KafkaConnectionTest {

    @Autowired
    private EmbeddedKafkaBroker embeddedKafka;

    @Test
    void shouldProduceAndConsumeMessage() throws Exception {
        // Given: Producer configuration
        Map<String, Object> producerProps = KafkaTestUtils.producerProps(embeddedKafka);
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        
        ProducerFactory<String, String> producerFactory = new DefaultKafkaProducerFactory<>(producerProps);
        KafkaTemplate<String, String> kafkaTemplate = new KafkaTemplate<>(producerFactory);

        // Given: Consumer configuration
        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("test-group", "true", embeddedKafka);
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        
        ConsumerFactory<String, String> consumerFactory = new DefaultKafkaConsumerFactory<>(consumerProps);
        var consumer = consumerFactory.createConsumer();
        consumer.subscribe(Collections.singletonList(KafkaTopics.INGREDIENT_SUBMITTED));

        // When: Produce message
        String testMessage = "{\"userId\":\"user-123\",\"ingredients\":[\"chicken\",\"garlic\"]}";
        kafkaTemplate.send(KafkaTopics.INGREDIENT_SUBMITTED, "user-123", testMessage).get(5, TimeUnit.SECONDS);

        // Then: Consume and verify message
        ConsumerRecord<String, String> record = 
            KafkaTestUtils.getSingleRecord(consumer, KafkaTopics.INGREDIENT_SUBMITTED, Duration.ofSeconds(10));
        
        assertThat(record).isNotNull();
        assertThat(record.key()).isEqualTo("user-123");
        assertThat(record.value()).contains("user-123");
        assertThat(record.value()).contains("chicken");
        
        consumer.close();
    }

    @Test
    void shouldVerifyAllTopicsExist() {
        // Given: Expected topics
        List<String> expectedTopics = List.of(
            KafkaTopics.INGREDIENT_SUBMITTED,
            KafkaTopics.RECIPE_MATCHED,
            KafkaTopics.SUBSTITUTION_REQUESTED,
            KafkaTopics.USER_PREFERENCE_UPDATED,
            KafkaTopics.ANALYTICS_EVENT
        );

        // When: Get topics from embedded broker
        var topics = embeddedKafka.getTopics();

        // Then: Verify all expected topics exist
        assertThat(topics).containsAll(expectedTopics);
    }
}

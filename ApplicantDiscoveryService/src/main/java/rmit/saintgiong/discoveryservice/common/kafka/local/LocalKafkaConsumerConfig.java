package rmit.saintgiong.discoveryservice.common.kafka.local;


import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import rmit.saintgiong.shared.type.KafkaTopic;

import java.util.Map;
import java.util.UUID;

@Configuration
public class LocalKafkaConsumerConfig {

    private final LocalKafkaConfig localKafkaConfig;

    public LocalKafkaConsumerConfig(LocalKafkaConfig localKafkaConfig) {
        this.localKafkaConfig = localKafkaConfig;
    }

    @Bean(name = "localConsumerFactory")
    public ConsumerFactory<String, Object> localConsumerFactory() {
        Map<String, Object> config = localKafkaConfig.getLocalConfigs();

        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class);

        // Crucial for mapping Avro to your Java DTOs
        config.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, true);

        // "earliest" ensures you don't miss messages if the app starts fresh
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        
        // Add group.id
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "discovery-service-group-" + UUID.randomUUID());

        return new DefaultKafkaConsumerFactory<>(config);
    }

    @Bean(name = "localKafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, Object> localKafkaListenerContainerFactory(
            @Qualifier("localKafkaTemplate") KafkaTemplate<String, Object> kafkaTemplate) {

        ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setReplyTemplate(kafkaTemplate);
        factory.setConsumerFactory(localConsumerFactory());

        return factory;
    }

    // --- REQUEST-REPLY CONFIGURATION ---

    @Bean
    public ConcurrentMessageListenerContainer<String, Object> localKafkaReplyContainer(
            @Qualifier("localConsumerFactory") ConsumerFactory<String, Object> consumerFactory) {

        ContainerProperties containerProperties = new ContainerProperties(
                KafkaTopic.JM_PREMIUM_COMPANY_LIST_RESPONSE_TOPIC
        );

        String uniqueGroupId = "discovery-reply-group-" + UUID.randomUUID();
        containerProperties.setGroupId(uniqueGroupId);

        return new ConcurrentMessageListenerContainer<>(consumerFactory, containerProperties);
    }

}

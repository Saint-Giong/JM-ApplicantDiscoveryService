package rmit.saintgiong.discoveryservice.common.kafka.cloud;


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
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import rmit.saintgiong.discoveryapi.internal.common.types.type.KafkaTopic;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;

@Configuration
public class CloudKafkaConsumerConfig {

    private final CloudKafkaConfig cloudKafkaConfig;

    public CloudKafkaConsumerConfig(CloudKafkaConfig cloudKafkaConfig) {
        this.cloudKafkaConfig = cloudKafkaConfig;
    }

    @Bean(name = "cloudConsumerFactory")
    public ConsumerFactory<String, Object> cloudConsumerFactory() {
        Map<String, Object> config = cloudKafkaConfig.getCloudConfigs();

        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class);

        // Crucial for mapping Avro to your Java DTOs
        config.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, true);

        // "earliest" ensures you don't miss messages if the app starts fresh
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "discovery-service-cloud-consumer-group");

        return new DefaultKafkaConsumerFactory<>(config);
    }

    @Bean(name = "cloudKafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, Object> cloudKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(cloudConsumerFactory());
        return factory;
    }

    // --- REQUEST-REPLY CONFIGURATION ---

//    @Bean
//    public ConcurrentMessageListenerContainer<String, Object> cloudKafkaReplyContainer(
//            @Qualifier("cloudConsumerFactory") ConsumerFactory<String, Object> consumerFactory) {
//
//        ContainerProperties containerProperties = new ContainerProperties(
//                KafkaTopic.ADD_APPLICANT_TOPIC_REPLIED,
//                KafkaTopic.UPDATE_APPLICANT_TOPIC_REPLIED,
//                KafkaTopic.DELETE_APPLICANT_TOPIC_REPLIED
//        );
//
//        // FIX: Unique Group ID per instance prevents "reply stealing"
//        String uniqueGroupId = "reply-group-" + UUID.randomUUID();
//        containerProperties.setGroupId(uniqueGroupId);
//
////         Optimization: Replies are ephemeral; we don't need to commit offsets strictly
////        containerProperties.setAckMode(ContainerProperties.AckMode.RECORD);
//
//        return new ConcurrentMessageListenerContainer<>(consumerFactory, containerProperties);
//    }
//
//    @Bean
//    public ReplyingKafkaTemplate<String, Object, Object> cloudReplyingKafkaTemplate(
//            @Qualifier("cloudProducerFactory") ProducerFactory<String, Object> pf,
//            @Qualifier("cloudKafkaReplyContainer") ConcurrentMessageListenerContainer<String, Object> replyContainer) {
//
//        ReplyingKafkaTemplate<String, Object, Object> template = new ReplyingKafkaTemplate<>(pf, replyContainer);
//
//        // Timeout: If the other service takes > 10s, throw an error
//        template.setDefaultReplyTimeout(Duration.ofSeconds(10));
//
//        // Ensure shared reply topic headers are handled correctly
//        template.setSharedReplyTopic(true);
//
//        return template;
//    }
}
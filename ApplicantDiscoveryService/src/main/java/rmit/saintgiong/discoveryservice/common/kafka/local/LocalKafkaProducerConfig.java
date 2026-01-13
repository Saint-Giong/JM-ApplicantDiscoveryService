package rmit.saintgiong.discoveryservice.common.kafka.local;

import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;

import java.time.Duration;
import java.util.Map;

@Configuration
public class LocalKafkaProducerConfig {

    private final LocalKafkaConfig localKafkaConfig;

    public LocalKafkaProducerConfig(LocalKafkaConfig localKafkaConfig) {
        this.localKafkaConfig = localKafkaConfig;
    }

    @Bean(name = "localProducerFactory")
    public ProducerFactory<String, Object> localProducerFactory() {
        Map<String, Object> config = localKafkaConfig.getLocalConfigs();
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean(name = "localKafkaTemplate")
    public KafkaTemplate<String, Object> localKafkaTemplate() {
        return new KafkaTemplate<>(localProducerFactory());
    }


    @Bean
    public ReplyingKafkaTemplate<String, Object, Object> localReplyingKafkaTemplate(
            @Qualifier("localProducerFactory") ProducerFactory<String, Object> pf,
            @Qualifier("localKafkaReplyContainer") ConcurrentMessageListenerContainer<String, Object> replyContainer) {

        ReplyingKafkaTemplate<String, Object, Object> template = new ReplyingKafkaTemplate<>(pf, replyContainer);
        // Timeout: If the other service takes > 10s, throw an error
        template.setDefaultReplyTimeout(Duration.ofSeconds(10));

        return template;
    }
}

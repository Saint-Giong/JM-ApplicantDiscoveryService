package rmit.saintgiong.discoveryservice.common.kafka.cloud;

import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.Map;

@Configuration
public class CloudKafkaProducerConfig {

    private final CloudKafkaConfig cloudKafkaConfig;

    public CloudKafkaProducerConfig(CloudKafkaConfig cloudKafkaConfig) {
        this.cloudKafkaConfig = cloudKafkaConfig;
    }

    @Bean(name = "cloudProducerFactory")
    public ProducerFactory<String, Object> cloudProducerFactory() {
        Map<String, Object> config = cloudKafkaConfig.getCloudConfigs();
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean(name = "cloudKafkaTemplate")
    public KafkaTemplate<String, Object> cloudKafkaTemplate() {
        return new KafkaTemplate<>(cloudProducerFactory());
    }
}
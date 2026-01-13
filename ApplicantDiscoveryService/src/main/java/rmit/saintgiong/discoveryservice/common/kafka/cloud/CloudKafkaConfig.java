package rmit.saintgiong.discoveryservice.common.kafka.cloud;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.common.config.SaslConfigs;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class CloudKafkaConfig {

    private final String bootstrapServers;
    private final String schemaRegistryUrl;
    private final String apiKey;
    private final String apiSecret;
    private final String schemaRegistryKey;
    private final String schemaRegistrySecret;

    public CloudKafkaConfig(
            @Value("${kafka.cloud.bootstrap-servers}") String bootstrapServers,
            @Value("${kafka.cloud.schema-registry.url}") String schemaRegistryUrl,
            @Value("${kafka.cloud.security.api-key}") String apiKey,
            @Value("${kafka.cloud.security.api-secret}") String apiSecret,
            @Value("${kafka.cloud.schema-registry.api-key}") String schemaRegistryKey,
            @Value("${kafka.cloud.schema-registry.api-secret}") String schemaRegistrySecret) {
        this.bootstrapServers = bootstrapServers;
        this.schemaRegistryUrl = schemaRegistryUrl;
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
        this.schemaRegistryKey = schemaRegistryKey;
        this.schemaRegistrySecret = schemaRegistrySecret;
    }


    //Configure Schema Registry & SASL
    public Map<String, Object> getCloudConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_SSL");
        props.put(SaslConfigs.SASL_MECHANISM, "PLAIN");
        props.put(SaslConfigs.SASL_JAAS_CONFIG,
                String.format("org.apache.kafka.common.security.plain.PlainLoginModule required username=\"%s\" password=\"%s\";", apiKey, apiSecret));

        // Schema Registry
        props.put("schema.registry.url", schemaRegistryUrl);
        props.put("basic.auth.credentials.source", "USER_INFO");
        props.put("basic.auth.user.info", schemaRegistryKey + ":" + schemaRegistrySecret);
        return props;
    }

}

package rmit.saintgiong.discoveryservice.common.kafka.local;

import org.apache.kafka.clients.CommonClientConfigs;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class LocalKafkaConfig {

    private final String bootstrapServers;
    private final String schemaRegistryUrl;


    public LocalKafkaConfig(
            @Value("${kafka.local.bootstrap-servers}") String bootstrapServers,
            @Value("${kafka.local.schema-registry.url}") String schemaRegistryUrl) {
        this.bootstrapServers = bootstrapServers;
        this.schemaRegistryUrl = schemaRegistryUrl;
    }


    //Configure Schema Registry
    public Map<String, Object> getLocalConfigs() {
        Map<String, Object> props = new HashMap<>();

        props.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

        // Schema Registry
        props.put("schema.registry.url", schemaRegistryUrl);

        return props;
    }

}

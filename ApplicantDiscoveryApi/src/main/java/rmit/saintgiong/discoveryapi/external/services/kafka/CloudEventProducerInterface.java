package rmit.saintgiong.discoveryapi.external.services.kafka;

public interface CloudEventProducerInterface {
    void send(String requestTopic, Object requestData);
}

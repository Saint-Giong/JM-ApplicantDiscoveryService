package rmit.saintgiong.discoveryservice.domain.services.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import rmit.saintgiong.discoveryapi.internal.common.types.type.KafkaTopic;
import rmit.saintgiong.discoveryapi.internal.service.elasticsearch.IndexingInterface;
import org.springframework.kafka.core.KafkaTemplate;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rmit.saintgiong.discoveryapi.external.common.dto.avro.*;
@Service
public class CloudKafkaConsumer {

    private static final Logger log = LoggerFactory.getLogger(CloudKafkaConsumer.class);
    private final IndexingInterface indexingInterface;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public CloudKafkaConsumer(IndexingInterface indexingInterface, @Qualifier("cloudKafkaTemplate") KafkaTemplate<String, Object> kafkaTemplate) {
        this.indexingInterface = indexingInterface;
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(
            topics = {
                KafkaTopic.ADD_APPLICANT_TOPIC_REQUEST,
                KafkaTopic.UPDATE_APPLICANT_TOPIC_REQUEST
            },
            containerFactory = "cloudKafkaListenerContainerFactory"
    )
    public void consumeApplicantUpdate(ConsumerRecord<String, ApplicantProfileRecord> record) {
        ApplicantProfileRecord applicantProfileRecord = record.value();
        String applicantId = String.valueOf(applicantProfileRecord.getApplicantId());
        try {
            // IndexingService handles both add and update (upsert)
            indexingInterface.indexApplicant(applicantProfileRecord);
            
            // Determine reply topic
            String replyTopic = record.topic().equals(KafkaTopic.ADD_APPLICANT_TOPIC_REQUEST) 
                ? KafkaTopic.ADD_APPLICANT_TOPIC_REPLIED 
                : KafkaTopic.UPDATE_APPLICANT_TOPIC_REPLIED;

            sendReply(replyTopic, applicantId, "SUCCESS", "Applicant processed successfully", record.headers());

        } catch (Exception e) {
            log.error("Error processing applicant update/add: {}", e.getMessage());
             String replyTopic = record.topic().equals(KafkaTopic.ADD_APPLICANT_TOPIC_REQUEST)
                    ? KafkaTopic.ADD_APPLICANT_TOPIC_REPLIED
                    : KafkaTopic.UPDATE_APPLICANT_TOPIC_REPLIED;
            sendReply(replyTopic, applicantId, "FAILURE", e.getMessage(), record.headers());
        }
    }

    @KafkaListener(
            topics = KafkaTopic.DELETE_APPLICANT_TOPIC_REQUEST,
            containerFactory = "cloudKafkaListenerContainerFactory"
    )
    public void consumeApplicantDelete(ConsumerRecord<String, ApplicantProfileRecord> record) {
        // For delete, we might only get the ID, but assuming we get an ApplicantAvro with ID populated
        ApplicantProfileRecord applicantProfileRecord = record.value();
        String applicantId = String.valueOf(applicantProfileRecord.getApplicantId());
        try {
            indexingInterface.deleteApplicant(applicantId);
            sendReply(KafkaTopic.DELETE_APPLICANT_TOPIC_REPLIED, applicantId, "SUCCESS", "Applicant deleted successfully", record.headers());
        } catch (Exception e) {
            log.error("Error processing applicant delete: {}", e.getMessage());
            sendReply(KafkaTopic.DELETE_APPLICANT_TOPIC_REPLIED, applicantId, "FAILURE", e.getMessage(), record.headers());
        }
    }

    private void sendReply(String topic, String applicantId, String status, String message,  Headers headers) {
        ApplicantProfileRepliedRecord reply = ApplicantProfileRepliedRecord.newBuilder()
                .setApplicantId(applicantId)
                .setStatus(status)
                .setMessage(message)
                .setTimestamp(Instant.now().toEpochMilli())
                .build();

        ProducerRecord<String, Object> record = new ProducerRecord<>(topic, applicantId, reply);
        if (headers != null) {
            headers.forEach(header -> record.headers().add(header));
        }
        kafkaTemplate.send(record);
    }
}


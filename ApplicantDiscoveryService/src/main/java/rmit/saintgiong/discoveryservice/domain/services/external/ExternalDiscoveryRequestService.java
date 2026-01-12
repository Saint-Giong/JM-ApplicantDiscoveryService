package rmit.saintgiong.discoveryservice.domain.services.external;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import rmit.saintgiong.discoveryapi.external.services.ExternalDiscoveryRequestInterface;
import rmit.saintgiong.discoveryapi.external.services.kafka.CloudEventProducerInterface;
import rmit.saintgiong.discoveryapi.external.services.kafka.EventProducerInterface;
import rmit.saintgiong.shared.type.KafkaTopic;
// import rmit.saintgiong.jobpostapi.external.dto.avro.*; // Avro classes need to be generated or imported
// import rmit.saintgiong.shared.type.KafkaTopic; 

import rmit.saintgiong.discoveryapi.external.dto.avro.GetAllPremiumCompaniesRequestRecord;
import rmit.saintgiong.discoveryapi.external.dto.avro.GetAllPremiumCompaniesResponseRecord;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ExternalDiscoveryRequestService implements ExternalDiscoveryRequestInterface {

//    private final CloudEventProducerInterface cloudEventProducerInterface;
    private final EventProducerInterface eventProducer;
   
    public ExternalDiscoveryRequestService( EventProducerInterface eventProducer) {
//        this.cloudEventProducerInterface = cloudEventProducerInterface;
        this.eventProducer = eventProducer;
    }


    @Override
    public List<Object> sendGetAllPremiumCompaniesRequest() {
        try {
            GetAllPremiumCompaniesRequestRecord request = GetAllPremiumCompaniesRequestRecord.newBuilder()
                    .setRequestId(UUID.randomUUID())
                    .build();

            // 2. Send and wait for reply
            GetAllPremiumCompaniesResponseRecord response = eventProducer.sendAndReceive(
                    KafkaTopic.JM_PREMIUM_COMPANY_LIST_REQUEST_TOPIC,
                    KafkaTopic.JM_PREMIUM_COMPANY_LIST_RESPONSE_TOPIC,
                    request,
                    GetAllPremiumCompaniesResponseRecord.class
            );

            if (response == null || response.getPremiumCompanyIds().isEmpty()) {
                List<Object > list = new ArrayList<>();
                list.add("22222222-2222-2222-2222-222222222222");
                return list;
//                return Collections.emptyList();
            }

            // 3. Map the Avro List to DTO List
            return response.getPremiumCompanyIds().stream()
                    .map(Object::toString)
                    .collect(Collectors.toList());

        } catch (ExecutionException | InterruptedException e) {
            log.error("Failed to fetch all profiles via Kafka", e);
            return Collections.emptyList();
        } catch (Exception e) {
            log.error("Unexpected error in sendGetAllProfilesRequest", e);
            return Collections.emptyList();
        }
    }

    public void sendMatchNotification(rmit.saintgiong.discoveryapi.external.dto.avro.ApplicantMatchNotificationRecord notification, boolean isUpdate) {
        String topic = isUpdate ? KafkaTopic.JM_UPDATE_APPLICANT_REQUEST_TOPIC : KafkaTopic.JM_NEW_APPLICANT_REQUEST_TOPIC;
        try {
            eventProducer.send(topic, notification);
            log.info("Sent match notification to topic: {}", topic);
        } catch (Exception e) {
            log.error("Failed to send match notification to topic: {}", topic, e);
        }
    }
}

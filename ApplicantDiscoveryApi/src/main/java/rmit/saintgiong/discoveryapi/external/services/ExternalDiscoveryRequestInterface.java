package rmit.saintgiong.discoveryapi.external.services;

import java.util.List;
import java.util.UUID;

import rmit.saintgiong.discoveryapi.external.dto.avro.ApplicantMatchNotificationRecord;

public interface ExternalDiscoveryRequestInterface {
    List<Object> sendGetAllPremiumCompaniesRequest();
    void sendMatchNotification(ApplicantMatchNotificationRecord notification, boolean isUpdate);
}

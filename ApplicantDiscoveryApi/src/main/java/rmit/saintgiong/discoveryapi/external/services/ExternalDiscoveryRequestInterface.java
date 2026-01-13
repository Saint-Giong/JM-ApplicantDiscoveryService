package rmit.saintgiong.discoveryapi.external.services;

import rmit.saintgiong.shared.dto.avro.notification.ApplicantMatchNotificationRecord;

import java.util.List;


public interface ExternalDiscoveryRequestInterface {
    List<Object> sendGetAllPremiumCompaniesRequest();
    void sendMatchNotification(ApplicantMatchNotificationRecord notification, boolean isUpdate);
}

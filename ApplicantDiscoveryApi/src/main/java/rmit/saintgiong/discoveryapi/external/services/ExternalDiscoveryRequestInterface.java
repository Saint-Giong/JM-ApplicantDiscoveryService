package rmit.saintgiong.discoveryapi.external.services;

import rmit.saintgiong.shared.dto.avro.notification.ApplicantMatchNotificationRecord;

import java.util.List;
import java.util.UUID;


public interface ExternalDiscoveryRequestInterface {
    List<Object> sendGetAllPremiumCompaniesRequest();
    Boolean sendGetCompanyPremiumStatusRequest(UUID companyId);
    void sendMatchNotification(ApplicantMatchNotificationRecord notification, boolean isUpdate);
}

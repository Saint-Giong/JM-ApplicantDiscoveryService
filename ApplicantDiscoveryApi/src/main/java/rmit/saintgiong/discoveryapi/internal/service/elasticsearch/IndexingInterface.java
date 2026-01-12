package rmit.saintgiong.discoveryapi.internal.service.elasticsearch;

import rmit.saintgiong.discoveryapi.internal.document.ApplicantDocument;

public interface IndexingInterface {
    void indexApplicant(ApplicantDocument document);
    void deleteApplicant(String applicantId);
}
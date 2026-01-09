package rmit.saintgiong.discoveryapi.internal.service.elasticsearch;

public interface IndexingInterface {
    void indexApplicant(Object applicantData);
    void deleteApplicant(String applicantId);
}

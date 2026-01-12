package rmit.saintgiong.discoveryapi.internal.common.types.type;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public final class KafkaTopic {
    public static final String ADD_APPLICANT_TOPIC_REQUEST = "JA_APPLICANT_CREATED";
    public static final String ADD_APPLICANT_TOPIC_REPLIED = "JA_ADD_APPLICANT_REPLIED";

    public static final String UPDATE_APPLICANT_TOPIC_REQUEST = "JA_APPLICANT_UPDATED";
    public static final String UPDATE_APPLICANT_TOPIC_REPLIED = "JA_UPDATE_APPLICANT_REPLIED";

    public static final String DELETE_APPLICANT_TOPIC_REQUEST = "JA_APPLICANT_DELETED";
    public static final String DELETE_APPLICANT_TOPIC_REPLIED = "JA_DELETE_APPLICANT_REPLIED";

}

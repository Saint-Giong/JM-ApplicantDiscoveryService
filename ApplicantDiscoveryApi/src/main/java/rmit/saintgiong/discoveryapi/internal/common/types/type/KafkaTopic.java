package rmit.saintgiong.discoveryapi.internal.common.types.type;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public final class KafkaTopic {
    public static final String ADD_APPLICANT_TOPIC_REQUEST = "JA_ADD_APPLICANT_REQUEST";
    public static final String ADD_APPLICANT_TOPIC_REPLIED = "JA_ADD_APPLICANT_REPLIED";

    public static final String UPDATE_APPLICANT_TOPIC_REQUEST = "JA_UPDATE_APPLICANT_REQUEST";
    public static final String UPDATE_APPLICANT_TOPIC_REPLIED = "JA_UPDATE_APPLICANT_REPLIED";

    public static final String DELETE_APPLICANT_TOPIC_REQUEST = "JA_DELETE_APPLICANT_REQUEST";
    public static final String DELETE_APPLICANT_TOPIC_REPLIED = "JA_DELETE_APPLICANT_REPLIED";

}

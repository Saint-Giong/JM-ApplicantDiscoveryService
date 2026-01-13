package rmit.saintgiong.discoveryservice.domain.dto.external;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Country(
    String name,
    String code
) {}

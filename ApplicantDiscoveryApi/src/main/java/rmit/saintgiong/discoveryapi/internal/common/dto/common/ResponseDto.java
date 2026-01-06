package rmit.saintgiong.discoveryapi.internal.common.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ResponseDto {
    private String code;
    private String message;
}

package rmit.saintgiong.discoveryapi.internal.service;

import rmit.saintgiong.discoveryapi.internal.dto.SearchProfileResponseDto;

import java.util.List;
import java.util.UUID;

public interface InternalGetSearchProfileInterface {
    SearchProfileResponseDto getSearchProfileById(UUID profileId);
    List<SearchProfileResponseDto> getSearchProfilesByCompanyId(UUID companyId);
}

package rmit.saintgiong.discoveryapi.internal.service;

import rmit.saintgiong.discoveryapi.internal.common.dto.CreateSearchProfileRequestDto;
import rmit.saintgiong.discoveryapi.internal.common.dto.SearchProfileResponseDto;


public interface InternalCreateSearchProfileInterface {
    SearchProfileResponseDto createSearchProfile(CreateSearchProfileRequestDto request);

}

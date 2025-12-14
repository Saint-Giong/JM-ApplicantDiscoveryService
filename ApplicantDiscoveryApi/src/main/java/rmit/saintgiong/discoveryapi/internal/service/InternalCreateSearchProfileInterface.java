package rmit.saintgiong.discoveryapi.internal.service;

import rmit.saintgiong.discoveryapi.internal.dto.CreateSearchProfileRequestDto;
import rmit.saintgiong.discoveryapi.internal.dto.SearchProfileResponseDto;

public interface InternalCreateSearchProfileInterface {
    SearchProfileResponseDto createSearchProfile(CreateSearchProfileRequestDto request);
}

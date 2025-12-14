package rmit.saintgiong.discoveryapi.internal.service;

import jakarta.transaction.Transactional;
import rmit.saintgiong.discoveryapi.internal.dto.CreateSearchProfileRequestDto;
import rmit.saintgiong.discoveryapi.internal.dto.SearchProfileResponseDto;

import java.util.UUID;

public interface InternalCreateSearchProfileInterface {
    SearchProfileResponseDto createSearchProfile(CreateSearchProfileRequestDto request);

}

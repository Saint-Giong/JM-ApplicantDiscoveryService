package rmit.saintgiong.discoveryapi.internal.service;

import rmit.saintgiong.discoveryapi.internal.dto.SearchProfileResponseDto;
import rmit.saintgiong.discoveryapi.internal.dto.UpdateSearchProfileRequestDto;

import java.util.UUID;

public interface InternalUpdateSearchProfileInterface {
    SearchProfileResponseDto updateSearchProfile(UUID profileId, UpdateSearchProfileRequestDto request);
}

package rmit.saintgiong.discoveryapi.internal.service;

import rmit.saintgiong.discoveryapi.internal.common.dto.SearchProfileResponseDto;
import rmit.saintgiong.discoveryapi.internal.common.dto.UpdateSearchProfileRequestDto;

import java.util.UUID;

public interface InternalUpdateSearchProfileInterface {
    SearchProfileResponseDto updateSearchProfile(UUID profileId, UpdateSearchProfileRequestDto request);
}

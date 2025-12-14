package rmit.saintgiong.discoveryservice.searchprofile.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rmit.saintgiong.discoveryapi.internal.dto.UpdateSearchProfileRequestDto;
import rmit.saintgiong.discoveryapi.internal.service.InternalUpdateSearchProfileInterface;
import rmit.saintgiong.discoveryapi.internal.dto.SearchProfileResponseDto;
import rmit.saintgiong.discoveryservice.searchprofile.mapper.SearchProfileMapper;

import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class UpdateSearchProfileService implements InternalUpdateSearchProfileInterface {

    private final SearchProfileRepository searchProfileRepository;
    private final SearchProfileMapper searchProfileMapper;

    @Override
    @Transactional
    public SearchProfileResponseDto updateSearchProfile(UUID profileId, UpdateSearchProfileRequestDto request) {

        return null;
    }
}

package rmit.saintgiong.discoveryservice.searchprofile.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rmit.saintgiong.discoveryapi.internal.service.InternalCreateSearchProfileInterface;
import rmit.saintgiong.discoveryapi.internal.dto.CreateSearchProfileRequestDto;
import rmit.saintgiong.discoveryapi.internal.dto.SearchProfileResponseDto;
import rmit.saintgiong.discoveryservice.searchprofile.mapper.SearchProfileMapper;

@Service
@AllArgsConstructor
@Slf4j
public class CreateSearchProfileService implements InternalCreateSearchProfileInterface {

    private final SearchProfileRepository searchProfileRepository;
    private final SearchProfileMapper searchProfileMapper;

    @Override
    @Transactional
    public SearchProfileResponseDto createSearchProfile(CreateSearchProfileRequestDto request) {

        return null;
    }
}

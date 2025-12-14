package rmit.saintgiong.discoveryservice.searchprofile.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rmit.saintgiong.discoveryapi.internal.service.InternalGetSearchProfileInterface;
import rmit.saintgiong.discoveryapi.internal.dto.SearchProfileResponseDto;
import rmit.saintgiong.discoveryservice.searchprofile.mapper.SearchProfileMapper;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class GetSearchProfileService implements InternalGetSearchProfileInterface {

    private final SearchProfileRepository searchProfileRepository;
    private final SearchProfileMapper searchProfileMapper;

    @Override
    @Transactional(readOnly = true)
    public SearchProfileResponseDto getSearchProfileById(UUID profileId) {

        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public List<SearchProfileResponseDto> getSearchProfilesByCompanyId(UUID companyId) {

        return List.of();
    }
}


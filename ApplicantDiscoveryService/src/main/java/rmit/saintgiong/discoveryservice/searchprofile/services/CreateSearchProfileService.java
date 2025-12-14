package rmit.saintgiong.discoveryservice.searchprofile.services;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rmit.saintgiong.discoveryapi.internal.service.InternalCreateSearchProfileInterface;
import rmit.saintgiong.discoveryapi.internal.dto.CreateSearchProfileRequestDto;
import rmit.saintgiong.discoveryapi.internal.dto.SearchProfileResponseDto;
import rmit.saintgiong.discoveryservice.searchprofile.entity.SearchProfileEntity;
import rmit.saintgiong.discoveryservice.searchprofile.mapper.SearchProfileMapper;

import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class CreateSearchProfileService implements InternalCreateSearchProfileInterface {

    private final SearchProfileRepository searchProfileRepository;
    private final SearchProfileMapper searchProfileMapper;

    @Override
    @Transactional
    public SearchProfileResponseDto createSearchProfile(CreateSearchProfileRequestDto request) {

        SearchProfileEntity entity = searchProfileMapper.requestDtoToEntity(request);

        if (request.getSkillTagIds() != null) {
            for (Integer tagId : request.getSkillTagIds()) {
                entity.addSkillTag(tagId);
            }
        }

        SearchProfileEntity savedEntity = searchProfileRepository.save(entity);
        return searchProfileMapper.entityToResponseDto(savedEntity);
    }


}

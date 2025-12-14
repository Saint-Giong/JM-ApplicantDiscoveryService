package rmit.saintgiong.discoveryservice.searchprofile.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rmit.saintgiong.discoveryapi.internal.service.InternalDeleteSearchProfileInterface;

import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class DeleteSearchProfileService implements InternalDeleteSearchProfileInterface {

    private final SearchProfileRepository searchProfileRepository;

    @Override
    @Transactional
    public void deleteSearchProfile(UUID profileId) {

    }
}

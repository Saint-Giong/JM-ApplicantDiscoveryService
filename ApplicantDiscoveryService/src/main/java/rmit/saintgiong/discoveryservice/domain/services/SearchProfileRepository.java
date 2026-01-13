package rmit.saintgiong.discoveryservice.domain.services;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rmit.saintgiong.discoveryservice.domain.entity.SearchProfileEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface SearchProfileRepository extends JpaRepository<SearchProfileEntity, UUID> {
    List<SearchProfileEntity> findByCompanyId(UUID companyId);

    List<SearchProfileEntity> findByCompanyIdIn(List<UUID> companyIds);
}

package izanagi.domain.repository.program;

import izanagi.domain.entity.EpgAcquisitionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IEpgAcquisitionRepository extends JpaRepository<EpgAcquisitionEntity, Integer> {
}

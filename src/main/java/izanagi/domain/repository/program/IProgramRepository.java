package izanagi.domain.repository.program;

import izanagi.domain.entity.ProgramEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface IProgramRepository
    extends JpaRepository<ProgramEntity, UUID>, JpaSpecificationExecutor<ProgramEntity> {
  List<ProgramEntity> findByBeginLessThanAndEndGreaterThan(long begin, long end);
}

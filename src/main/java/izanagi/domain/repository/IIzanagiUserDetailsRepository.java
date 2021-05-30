package izanagi.domain.repository;

import izanagi.domain.entity.IzanagiUserDetailsEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IIzanagiUserDetailsRepository
    extends JpaRepository<IzanagiUserDetailsEntity, UUID> {
  IzanagiUserDetailsEntity findOneByUsername(String username);
}

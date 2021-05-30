package izanagi.domain.repository;

import izanagi.domain.entity.IzanagiUserRoleEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IIzanagiUserRoleRepository extends JpaRepository<IzanagiUserRoleEntity, UUID> {
  IzanagiUserRoleEntity findOneByName(String name);
}

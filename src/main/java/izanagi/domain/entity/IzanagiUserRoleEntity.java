package izanagi.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import java.util.Set;
import java.util.UUID;
import lombok.Data;

@Data
@Entity
public class IzanagiUserRoleEntity {

  @Id
  UUID uuid = UUID.randomUUID();

  @Column(unique = true)
  private String name;

  @Column(unique = true)
  private String authority;

  @ManyToMany(mappedBy = "izanagiUserRoleEntitySet", fetch = FetchType.EAGER)
  private Set<IzanagiUserDetailsEntity> izanagiUserDetailsEntitySet;
}

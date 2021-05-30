package izanagi.domain.entity;

import java.util.Set;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
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

package izanagi.domain.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.Data;

@Data
@Entity
public class EpgAcquisitionEntity {
  @Id
  Integer id = 0;
  long latest = 0;
}

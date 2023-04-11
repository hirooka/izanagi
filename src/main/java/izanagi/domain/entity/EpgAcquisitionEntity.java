package izanagi.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class EpgAcquisitionEntity {
  @Id
  Integer id = 0;
  long latest = 0;
}

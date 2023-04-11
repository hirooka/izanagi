package izanagi.domain.entity;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
public class AbstractEntity implements Serializable {

  @Id
  private UUID uuid = UUID.randomUUID();

  @Version
  private long version;

  @CreatedBy
  private String createdBy;

  @CreatedDate
  private Date createdDate;

  @LastModifiedBy
  private String lastModifiedBy;

  @LastModifiedDate
  private Date lastModifiedDate;

  public UUID getUuid() {
    return uuid;
  }
}

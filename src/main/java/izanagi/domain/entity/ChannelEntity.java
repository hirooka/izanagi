package izanagi.domain.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Entity
public class ChannelEntity extends AbstractEntity {

  @Getter
  @Setter
  @Column(unique = true, nullable = false)
  private String id; // epgdump id

  @Getter
  @Setter
  private int transportStreamId; // epgdump transport_stream_id

  @Getter
  @Setter
  private int originalNetworkId; // epgdump original_network_id

  @Getter
  @Setter
  private int serviceId; // epgdump service_id

  @Getter
  @Setter
  private String name; // epgdump name

  // satelliteinfo

  @Getter
  @Setter
  private int channelRecording;

  @Getter
  @Setter
  private int channelRemoteControl;

  @Getter
  @Setter
  @OneToMany(mappedBy = "channelEntity", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  private List<ProgramEntity> programEntityList; // epgdump programs
}

package izanagi.domain.model.epg;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class EpgdumpChannel {
  private String id;
  private int transportStreamId;
  private int originalNetworkId;
  private int serviceId;
  private String name;
  private List<EpgdumpProgram> programs;
}

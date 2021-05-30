package izanagi.domain.model.epg;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class EpgdumpProgramVideo {
  private String resolution;
  private String aspect;
}

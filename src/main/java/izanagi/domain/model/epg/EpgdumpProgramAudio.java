package izanagi.domain.model.epg;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class EpgdumpProgramAudio {
  private String type;
  private String langcode;
  private String extdesc;
}

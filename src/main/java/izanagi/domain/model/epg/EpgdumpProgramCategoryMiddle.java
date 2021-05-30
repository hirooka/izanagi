package izanagi.domain.model.epg;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class EpgdumpProgramCategoryMiddle {
  private String jaJp;
  private String en;
}

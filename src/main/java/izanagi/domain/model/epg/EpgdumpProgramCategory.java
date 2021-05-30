package izanagi.domain.model.epg;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class EpgdumpProgramCategory {
  private EpgdumpProgramCategoryLarge large;
  private EpgdumpProgramCategoryMiddle middle;
}

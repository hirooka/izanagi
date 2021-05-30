package izanagi.domain.model.epg;

import lombok.Data;

@Data
public class EpgdumpSettings {
  private String recxxxCommand;
  private String recxxxOutputPath;
  private String epgdumpCommand;
  private String epgdumpOutputPath;
}

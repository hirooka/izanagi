package izanagi.domain.model.epg;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class EpgdumpProgram {
  private String channel;
  private String title;
  private String detail;
  private List<EpgdumpProgramExtDetail> extdetail;
  private long start;
  private long end;
  private long duration;
  private List<EpgdumpProgramCategory> category;
  //private List<EpgdumpProgramAttachInfo> attachinfo;
  private EpgdumpProgramVideo video;
  private List<EpgdumpProgramAudio> audio;
  private boolean freeCA;
  private int eventId;
}

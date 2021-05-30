package izanagi.domain.model.program;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;
import lombok.Data;

@Schema(name = "Program", description = "program")
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class Program {

  private UUID uuid = UUID.randomUUID();

  @Schema(description = "id")
  private String id;
  private String channel;
  private String title;
  private String detail;
  //    private List<Item> extdetail;
  private long start;
  private long end;
  private long duration;
  //    private List<Category> category;
  //    private List<?> attachinfo;
  //    private Video video;
  //    private List<Audio> audio;
  //    private boolean freeCA;
  private int eventId;

  private long begin;
  //private int physicalChannel;
  private String channelName;
  private String beginDate;
  private String endDate;
  private int channelRecording;
  private int channelRemoteControl;
}

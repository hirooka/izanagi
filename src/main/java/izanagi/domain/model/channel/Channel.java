package izanagi.domain.model.channel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import izanagi.domain.model.program.Program;
import java.util.List;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class Channel {
  private String id;
  private int transportStreamId;
  private int originalNetworkId;
  private int serviceId;
  private String name;
  private List<Program> programs;

  private int channelRecording;
  private int channelRemoteControl;
}

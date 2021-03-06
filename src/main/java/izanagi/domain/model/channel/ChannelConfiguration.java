package izanagi.domain.model.channel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import izanagi.domain.model.tuner.TunerType;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class ChannelConfiguration {
  private TunerType type;
  private int channelRemoteControl;
  private int channelRecording;
  private int serviceId;
}

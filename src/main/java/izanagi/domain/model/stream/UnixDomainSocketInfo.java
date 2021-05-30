package izanagi.domain.model.stream;

import izanagi.domain.model.tuner.Tuner;
import lombok.Data;

@Data
public class UnixDomainSocketInfo {
  private String socketPath;
  private Tuner tuner;
  private int channelRecording;
}

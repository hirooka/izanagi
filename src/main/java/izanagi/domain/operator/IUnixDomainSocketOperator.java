package izanagi.domain.operator;

import izanagi.domain.model.stream.UnixDomainSocketInfo;
import izanagi.domain.model.tuner.Tuner;
import izanagi.domain.model.tuner.TunerStatus;

public interface IUnixDomainSocketOperator {
  boolean isEnabled();

  void open(Tuner tuner, int channelRecording, String socketPath, long duration);

  UnixDomainSocketInfo getUnixDomainSocketInfo(
      int channelRemoteControl, long duration, TunerStatus tunerStatus
  ) throws Exception;
}

package izanagi.domain.service.tuner;

import izanagi.domain.model.channel.ChannelConfiguration;
import izanagi.domain.model.tuner.Tuner;
import izanagi.domain.model.tuner.TunerStatus;
import izanagi.domain.model.tuner.TunerType;
import java.util.List;

public interface ITunerService {

  Tuner readSuitableOne(final TunerType tunerType, final TunerStatus tunerStatus);

  Tuner readClosableOne(final int channelRemoteControl, final TunerStatus tunerStatus);

  boolean lock(
      final Tuner tuner,
      final long pid,
      final int channelRecording,
      final TunerStatus tunerStatus,
      final Process process
  );

  boolean release(final Tuner tuner);

  void releaseAll();

  boolean isEpgAcquisitionRunning();

  List<ChannelConfiguration> getChannelConfigurationList();

  ChannelConfiguration getChannelConfiguration(final int channelRemoteControl);
}

package izanagi.domain.operator.program;

import izanagi.domain.model.program.Program;
import izanagi.domain.model.tuner.TunerType;
import java.util.List;

public interface IProgramOperator {
  List<Program> getByChannelRemoteControl(final int channelRemoteControl);

  Program getByChannelRemoteControlNow(final int channelRemoteControl);

  List<Program> getAllNow();

  List<Program> getEpgAllChannel();

  List<Program> getEpgByChannel(final TunerType tunerType, final int channelRecording);

  void acquireEpg();

  long getLatest();
}

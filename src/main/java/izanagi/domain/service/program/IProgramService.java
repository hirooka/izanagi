package izanagi.domain.service.program;

import izanagi.domain.entity.ProgramEntity;
import izanagi.domain.model.program.Program;
import java.util.List;

public interface IProgramService {
  Program create(Program program);

  Program create(ProgramEntity programEntity);

  List<Program> readByChannelRemoteControl(int channelRemoteControl);

  List<Program> readByChannelRecording(int channelRecording);

  Program readByChannelRemoteControlNow(int channelRemoteControl);

  Program readByChannelRecordingNow(int channelRecording);

  List<Program> readAllNow();

  Program update(Program program);

  void delete(Program program);

  List<Program> getAllNow();

  List<Program> getAllByChannelRemoteControl(int channelRemoteControl);
}

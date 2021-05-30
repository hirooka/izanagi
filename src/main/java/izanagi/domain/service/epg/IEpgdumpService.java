package izanagi.domain.service.epg;

import izanagi.domain.model.epg.EpgdumpSettings;
import izanagi.domain.model.tuner.Tuner;

public interface IEpgdumpService {
  EpgdumpSettings getEpgdumpSettings(Tuner tuner, int channelRecording);

  void done(long latest);

  long getLatest();

  void cleanupTemporaryEpgdumpPath();
}

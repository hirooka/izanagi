package izanagi.domain.service.epg;

import izanagi.domain.config.EpgdumpConfiguration;
import izanagi.domain.entity.EpgAcquisitionEntity;
import izanagi.domain.model.epg.EpgdumpSettings;
import izanagi.domain.model.tuner.Tuner;
import izanagi.domain.repository.program.IEpgAcquisitionRepository;
import java.io.File;
import java.io.IOException;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

@Slf4j
@AllArgsConstructor
@Service
public class EpgdumpService implements IEpgdumpService {

  private final String fileSeparator = File.separator;
  private final String epgdumpFilePrefix = "epgdump";
  private final String epgdumpFormat = "json";
  private final String epgdumpTsExtension = ".ts";
  private final String epgdumpJsonExtension = ".json";

  private final EpgdumpConfiguration epgdumpConfiguration;
  private final IEpgAcquisitionRepository epgRepository;

  @Override
  public EpgdumpSettings getEpgdumpSettings(Tuner tuner, int channelRecording) {

    cleanupTemporaryEpgdumpPath();

    final File temporaryEpgdumpPathFile = new File(epgdumpConfiguration.getEpgdumpTemporaryPath());
    if (temporaryEpgdumpPathFile.mkdirs()) {
      log.info("epgdump temporary path: {}", temporaryEpgdumpPathFile);
    } else {
      log.error("cannot create epgdump temporary path: {}", temporaryEpgdumpPathFile);

    }

    final String channelRecordingString = Integer.toString(channelRecording);
    final String durationString =
        Integer.toString(epgdumpConfiguration.getEpgdumpRecordingDuration());

    final String recxxxOutputPath = epgdumpConfiguration.getEpgdumpTemporaryPath() + fileSeparator
        + epgdumpFilePrefix + channelRecordingString + epgdumpTsExtension;
    final String recxxxCommand = tuner.getCommand()
        .replace("<channel>", channelRecordingString)
        .replace("<duration>", durationString)
        .replace("<destination>", recxxxOutputPath);

    final String epgdumpOutputPath = epgdumpConfiguration.getEpgdumpTemporaryPath() + fileSeparator
        + epgdumpFilePrefix + channelRecordingString + epgdumpJsonExtension;
    final String epgdumpCommand = epgdumpConfiguration.getEpgdumpPath() + " "
        + epgdumpFormat + " " + recxxxOutputPath + " " + epgdumpOutputPath;

    final EpgdumpSettings epgdumpSettings = new EpgdumpSettings();
    epgdumpSettings.setRecxxxOutputPath(recxxxOutputPath);
    epgdumpSettings.setRecxxxCommand(recxxxCommand);
    epgdumpSettings.setEpgdumpOutputPath(epgdumpOutputPath);
    epgdumpSettings.setEpgdumpCommand(epgdumpCommand);
    log.info(epgdumpSettings.toString());

    return epgdumpSettings;
  }

  @Override
  public void done(long latest) {
    final Optional<EpgAcquisitionEntity> epgEntity = epgRepository.findById(0);
    final EpgAcquisitionEntity toBeUpdatedEpgAcquisitionEntity;
    toBeUpdatedEpgAcquisitionEntity = epgEntity.orElseGet(EpgAcquisitionEntity::new);
    toBeUpdatedEpgAcquisitionEntity.setLatest(latest);
    epgRepository.save(toBeUpdatedEpgAcquisitionEntity);
  }

  @Override
  public long getLatest() {
    final Optional<EpgAcquisitionEntity> epgEntity = epgRepository.findById(0);
    final EpgAcquisitionEntity gottenEpgAcquisitionEntity;
    gottenEpgAcquisitionEntity = epgEntity.orElseGet(EpgAcquisitionEntity::new);
    return gottenEpgAcquisitionEntity.getLatest();
  }

  @Override
  public void cleanupTemporaryEpgdumpPath() {
    final File file = new File(epgdumpConfiguration.getEpgdumpTemporaryPath());
    try {
      if (file.exists()) {
        FileUtils.cleanDirectory(file);
        if (!file.delete()) {
          log.info("could not delete temporary epgdump path: {}", file.getAbsolutePath());
        }
      }
    } catch (IOException e) {
      log.info("could not clean temporary epgdump path:{} {}", e.getMessage(), e);
    }

  }
}

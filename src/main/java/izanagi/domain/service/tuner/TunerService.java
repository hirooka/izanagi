package izanagi.domain.service.tuner;

import static java.util.Objects.requireNonNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import izanagi.domain.config.TunerConfig;
import izanagi.domain.model.channel.ChannelConfiguration;
import izanagi.domain.model.channel.ChannelConfigurationWrapper;
import izanagi.domain.model.tuner.Tuner;
import izanagi.domain.model.tuner.TunerStatus;
import izanagi.domain.model.tuner.TunerType;
import izanagi.domain.model.tuner.TunerWrapper;
import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TunerService implements ITunerService {

  private final String fileSeparator = File.separator;

  private List<Tuner> tunerList = new ArrayList<>();

  @Value("${tuner.json}")
  private String tunerJson;

  private final TunerConfig tunerConfig;

  public TunerService(TunerConfig tunerConfig) {
    this.tunerConfig = requireNonNull(tunerConfig);
  }

  private List<Tuner> getTunerListFromTunerJson() {
    // TODO: classpath
    final String path =
        ITunerService.class.getProtectionDomain().getCodeSource().getLocation().getPath();
    final String[] pathArray = path.split(fileSeparator);
    final StringBuilder currentPath = new StringBuilder();
    for (int i = 1; i < pathArray.length - 3; i++) {
      currentPath.append(fileSeparator).append(pathArray[i]);
    }

    try {
      Resource resource = new FileSystemResource(currentPath + fileSeparator + tunerJson);
      if (!resource.exists()) {
        resource = new ClassPathResource(tunerJson);
      }
      final ObjectMapper objectMapper = new ObjectMapper();
      return objectMapper.readValue(resource.getInputStream(), TunerWrapper.class).getTunerList();
    } catch (IOException ex) {
      throw new IllegalStateException("Failed to parse " + tunerJson, ex);
    }
  }

  @PostConstruct
  void init() {
    tunerList = getTunerListFromTunerJson();
    log.info(tunerList.toString());
  }

  private Optional<Tuner> findSuitableTuner(
      final TunerType tunerType, final TunerStatus tunerStatus
  ) {
    return tunerList.stream()
        .filter(tuner -> tuner.getType() == tunerType)
        .filter(tuner -> tuner.getStatus() == tunerStatus)
        .min(Comparator.comparingInt(Tuner::getIndex));
  }

  private Optional<Tuner> getSuitableTuner(
      final TunerType tunerType, final TunerStatus tunerStatus
  ) {
    switch (tunerStatus) {
      case LIVE:
        Optional<Tuner> liveSuitable = findSuitableTuner(tunerType, TunerStatus.AVAILABLE);
        if (liveSuitable.isEmpty()) {
          liveSuitable = findSuitableTuner(tunerType, TunerStatus.EPG);
          liveSuitable.ifPresent(this::release);
        }
        return liveSuitable;
      case RECORDING:
        Optional<Tuner> recordingSuitable = findSuitableTuner(tunerType, TunerStatus.AVAILABLE);
        if (recordingSuitable.isEmpty()) {
          recordingSuitable = findSuitableTuner(tunerType, TunerStatus.EPG);
          recordingSuitable.ifPresent(this::release);
          if (recordingSuitable.isEmpty()) {
            recordingSuitable = findSuitableTuner(tunerType, TunerStatus.LIVE);
            recordingSuitable.ifPresent(this::release);
          }
        }
        return recordingSuitable;
      case EPG:
      default:
        return findSuitableTuner(tunerType, TunerStatus.AVAILABLE);
    }
  }

  @Override
  public Tuner readSuitableOne(final TunerType tunerType, final TunerStatus tunerStatus) {
    log.info("Find tuner {}, {}...", tunerType, tunerStatus);
    final String message = "No tuner is available for " + tunerStatus.name().toUpperCase() + ".";
    return getSuitableTuner(tunerType, tunerStatus)
        .orElseThrow(() -> new NoSuchElementException(message));
  }

  @Override
  public Tuner readClosableOne(final int channelRemoteControl, final TunerStatus tunerStatus) {
    for (final Tuner tuner : tunerList) {
      for (final ChannelConfiguration channelConfiguration : getChannelConfigurationList()) {
        if (channelRemoteControl == channelConfiguration.getChannelRemoteControl()
            && tuner.getChannelRecording() == channelConfiguration.getChannelRecording()
        ) {
          if (tuner.getStatus() == tunerStatus) {
            return tuner;
          }
        }
      }
    }
    final String message = "No tuner is available for " + channelRemoteControl
        + "(" + tunerStatus + ").";
    throw new NoSuchElementException(message);
  }

  @Override
  public boolean lock(
      final Tuner tuner,
      final long pid,
      final int channelRecording,
      final TunerStatus tunerStatus,
      final Process process
  ) {
    for (final Tuner t : tunerList) {
      if (t.getDeviceName().equals(tuner.getDeviceName())) {
        t.setStatus(tunerStatus);
        t.setPid(pid);
        t.setChannelRecording(channelRecording);
        t.setProcess(process);
        return true;
      }
    }
    return false;
  }

  private void reset(final Tuner tuner) {
    if (tuner.getProcess() != null) {
      tuner.getProcess().destroy();
    }
    tuner.setProcess(null);
    tuner.setChannelRecording(0);
    tuner.setPid(0);
    tuner.setStatus(TunerStatus.AVAILABLE);
  }

  @Override
  public boolean release(Tuner tuner) {
    for (final Tuner t : tunerList) {
      if (t.getDeviceName().equals(tuner.getDeviceName())) {
        reset(tuner);
        return true;
      }
    }
    return false;
  }

  @Override
  public void releaseAll() {
    for (final Tuner tuner : tunerList) {
      reset(tuner);
    }
  }

  @Override
  public boolean isEpgAcquisitionRunning() {
    return tunerList.stream().anyMatch(tuner -> tuner.getStatus() == TunerStatus.EPG);
  }

  @Override
  public List<ChannelConfiguration> getChannelConfigurationList() {
    try {
      final Resource resource = new ClassPathResource(tunerConfig.getChannelConfiguration());
      final ObjectMapper objectMapper = new ObjectMapper();
      return objectMapper.readValue(resource.getInputStream(), ChannelConfigurationWrapper.class)
          .getChannelConfigurationList();
    } catch (IOException ex) {
      throw new IllegalStateException("Failed to parse " + tunerJson, ex);
    }
  }

  @Override
  public ChannelConfiguration getChannelConfiguration(final int channelRemoteControl) {
    final List<ChannelConfiguration> channelConfigurationList = getChannelConfigurationList();
    for (final ChannelConfiguration channelConfiguration : channelConfigurationList) {
      if (channelConfiguration.getChannelRemoteControl() == channelRemoteControl) {
        return channelConfiguration;
      }
    }
    throw new IllegalStateException("Failed to find channelRemoteControl " + channelRemoteControl);
  }
}

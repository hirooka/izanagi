package izanagi.domain.operator.program;

import izanagi.domain.entity.ChannelEntity;
import izanagi.domain.model.channel.ChannelConfiguration;
import izanagi.domain.model.epg.EpgdumpSettings;
import izanagi.domain.model.program.Program;
import izanagi.domain.model.tuner.Tuner;
import izanagi.domain.model.tuner.TunerStatus;
import izanagi.domain.model.tuner.TunerType;
import izanagi.domain.service.epg.EpgdumpJsonParser;
import izanagi.domain.service.epg.IEpgdumpService;
import izanagi.domain.service.program.IChannelService;
import izanagi.domain.service.program.IProgramService;
import izanagi.domain.service.tuner.ITunerService;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

@Slf4j
@AllArgsConstructor
@EnableAsync
@Component
public class ProgramOperator implements IProgramOperator {

  private final IProgramService programService;
  private final ITunerService tunerService;
  private final IEpgdumpService epgdumpService;
  private final IChannelService channelService;

  @Override
  public List<Program> getByChannelRemoteControl(final int channelRemoteControl) {
    return programService.getAllByChannelRemoteControl(channelRemoteControl);
  }

  @Override
  public Program getByChannelRemoteControlNow(final int channelRemoteControl) {
    return programService.readByChannelRemoteControlNow(channelRemoteControl);
  }

  @Override
  public List<Program> getAllNow() {
    return programService.getAllNow();
  }

  @Override
  public List<Program> getEpgAllChannel() {
    List<Program> programList = new ArrayList<>();
    return programList;
  }

  @Override
  public List<Program> getEpgByChannel(final TunerType tunerType, final int channelRecording) {
    List<Program> programList = new ArrayList<>();
    return programList;
  }

  @Async
  @Override
  public void acquireEpg() {
    if (tunerService.isEpgAcquisitionRunning()) {
      return;
    }
    final List<ChannelConfiguration> channelConfigurationList =
        tunerService.getChannelConfigurationList();
    boolean isInterrupted = false;
    boolean isBsDone = false;
    for (final ChannelConfiguration channelConfiguration : channelConfigurationList) {
      if (channelConfiguration.getType() == TunerType.GR
          || channelConfiguration.getType() == TunerType.BS && !isBsDone
      ) {
        final int channelRemoteControl = channelConfiguration.getChannelRemoteControl();
        log.info("----------------------------------------------------------------");
        log.info("Channel Remote Control {} ({} / {})",
            channelRemoteControl,
            channelConfiguration.getType(),
            channelConfiguration.getChannelRecording()
        );

        try {
          final Tuner tuner = tunerService.readSuitableOne(
              channelConfiguration.getType(), TunerStatus.EPG
          );
          log.info("Tuner selected: {} {} {}",
              tuner.getType(), tuner.getDisplayName(), tuner.getDeviceName());
          log.info(tuner.toString());

          final int channelRecording = channelConfiguration.getChannelRecording();
          EpgdumpSettings epgdumpSettings =
              epgdumpService.getEpgdumpSettings(tuner, channelRecording);
          executeRecxxxCommand(epgdumpSettings.getRecxxxCommand(), tuner, channelRecording);
          final File recxxxOutput = new File(epgdumpSettings.getRecxxxOutputPath());

          if (recxxxOutput.exists() && recxxxOutput.length() > 0) {
            executeEpgdumpCommand(epgdumpSettings.getEpgdumpCommand());
            final File epgdumpOutput = new File(epgdumpSettings.getEpgdumpOutputPath());

            if (epgdumpOutput.exists() && epgdumpOutput.length() > 0) {
              final List<ChannelEntity> channelEntityList = channelService.getAll();
              final List<ChannelEntity> updatedChannelEntityList = EpgdumpJsonParser.parseJson(
                  epgdumpSettings.getEpgdumpOutputPath(),
                  channelConfigurationList,
                  channelEntityList
              );
              updatedChannelEntityList.forEach(channelService::create);
              if (channelConfiguration.getType() == TunerType.BS) {
                isBsDone = true;
              }
            } else {
              log.warn("No epgdump output: {}", epgdumpOutput.getAbsolutePath());
            }
          } else {
            log.warn("No recxxx output: {}", recxxxOutput.getAbsolutePath());
          }
        } catch (Exception ex) {
          log.warn("EGP acquisition has interrupted. {}", ex.getMessage());
          isInterrupted = true;
        }
      }
    }
    if (!isInterrupted) {
      // TODO: time per ch
      epgdumpService.done(Instant.now().toEpochMilli()); // TODO: -> service private
      log.info("EGP acquisition has completed.");
    }
    epgdumpService.cleanupTemporaryEpgdumpPath(); // TODO: -> service private
  }

  @Override
  public long getLatest() {
    return epgdumpService.getLatest();
  }

  // TODO: -> service
  private void executeRecxxxCommand(
      final String command, final Tuner tuner, final int channelRecording
  ) throws IOException {
    log.info("recxxx: {}", command);
    final String[] commandArray = command.split(" ");
    final ProcessBuilder processBuilder = new ProcessBuilder(commandArray);
    final Process process = processBuilder.start();
    final long pid = process.pid();
    // TODO: lock false
    tunerService.lock(tuner, pid, channelRecording, TunerStatus.EPG, process);
    try (final BufferedReader bufferedReader =
             new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
      String string;
      while ((string = bufferedReader.readLine()) != null) {
        log.info("{}", string);
      }
      bufferedReader.close();
      process.destroy();
      tunerService.release(tuner);
    }
  }

  // TODO: -> service
  private void executeEpgdumpCommand(final String command) throws IOException {
    log.info("epgdump: {}", command);
    final String[] commandArray = command.split(" ");
    final ProcessBuilder processBuilder = new ProcessBuilder(commandArray);
    final Process process = processBuilder.start();
    try (final BufferedReader bufferedReader =
             new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
      String string;
      while ((string = bufferedReader.readLine()) != null) {
        log.info("{}", string);
      }
      process.destroy();
    }
  }
}

package izanagi.domain.operator.stream;

import static izanagi.constants.IzanagiConstants.MPEG2_TS_PACKET_LENGTH;

import izanagi.domain.model.channel.ChannelConfiguration;
import izanagi.domain.model.tuner.Tuner;
import izanagi.domain.model.tuner.TunerStatus;
import izanagi.domain.model.tuner.TunerType;
import izanagi.domain.service.tuner.ITunerService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@Slf4j
@AllArgsConstructor
@Service
public class StreamOperator implements IStreamOperator {

  private final ITunerService tunerService;

  @Override
  public ResponseEntity<StreamingResponseBody> open(
      final int channelRemoteControl, final long duration, final TunerStatus tunerStatus
  ) {
    log.info("{}, {}, {}", channelRemoteControl, duration, tunerStatus);
    final ChannelConfiguration channelConfiguration =
        tunerService.getChannelConfiguration(channelRemoteControl);
    final TunerType tunerType = channelConfiguration.getType();
    final int channelRecording = channelConfiguration.getChannelRecording();

    final Tuner tuner = tunerService.readSuitableOne(tunerType, tunerStatus);
    log.info("Tuner selected: {}", tuner.toString());

    final String channelString = Integer.toString(channelRecording);
    final String durationString = duration < 0 ? "-" : Long.toString(duration);

    final String command = tuner.getCommand()
        .replace("<channel>", channelString)
        .replace("<duration>", durationString)
        .replace("<destination>", "-");
    final String[] commandArray = command.split(" ");

    return new ResponseEntity<>(outputStream -> {
      final ProcessBuilder processBuilder = new ProcessBuilder(commandArray);
      final Process process;
      try {
        process = processBuilder.start();
        final long pid = process.pid();
        // TODO: lock false
        tunerService.lock(tuner, pid, channelRecording, tunerStatus, process);
        final byte[] buf = new byte[MPEG2_TS_PACKET_LENGTH];
        int len;
        while ((len = process.getInputStream().read(buf)) != -1) {
          outputStream.write(buf, 0, len);
        }
        outputStream.close();
        process.destroy();
      } catch (Exception ex) {
        log.warn("{}", ex.getMessage());
      } finally {
        tunerService.release(tuner);
      }
    }, HttpStatus.OK);
  }

  @Override
  public ResponseEntity<Object> close(
      final int channelRemoteControl, final TunerStatus tunerStatus
  ) {
    final Tuner tuner = tunerService.readClosableOne(channelRemoteControl, tunerStatus);
    log.info("{}", tuner.toString());
    tunerService.release(tuner);
    return ResponseEntity.noContent().build();
  }
}

package izanagi.api.v1;

import izanagi.domain.model.stream.UnixDomainSocketInfo;
import izanagi.domain.model.tuner.TunerStatus;
import izanagi.domain.operator.IUnixDomainSocketOperator;
import izanagi.domain.operator.stream.IStreamOperator;
import java.nio.charset.StandardCharsets;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@Slf4j
@AllArgsConstructor
@RequestMapping("api/v1/streams")
@RestController
public class StreamRestController {

  private final IStreamOperator streamOperator;
  private final IUnixDomainSocketOperator unixDomainSocketOperator;

  @GetMapping("{channelRemoteControl}")
  public ResponseEntity<StreamingResponseBody> start(
      @PathVariable final int channelRemoteControl
  ) {
    log.info("[Start LIVE] {}ch", channelRemoteControl);
    return getStream(channelRemoteControl, -1, TunerStatus.LIVE);
  }

  @GetMapping("{channelRemoteControl}/{duration}")
  public ResponseEntity<StreamingResponseBody> start(
      @PathVariable final int channelRemoteControl, @PathVariable long duration
  ) {
    log.info("[Start RECORDING] {}ch, {}s", channelRemoteControl, duration);
    return getStream(channelRemoteControl, duration, TunerStatus.RECORDING);
  }

  private ResponseEntity<StreamingResponseBody> getStream(
      final int channelRemoteControl, final long duration, final TunerStatus tunerStatus
  ) {
    // TODO: -> Operator
    if (unixDomainSocketOperator.isEnabled()) {
      log.info("Unix Domain Socket mode");
      try {
        final UnixDomainSocketInfo unixDomainSocketInfo =
            unixDomainSocketOperator.getUnixDomainSocketInfo(
                channelRemoteControl, duration, tunerStatus
            );
        final String socketPath = unixDomainSocketInfo.getSocketPath();
        log.info("{}", unixDomainSocketInfo);
        unixDomainSocketOperator.open(
            unixDomainSocketInfo.getTuner(),
            unixDomainSocketInfo.getChannelRecording(),
            socketPath,
            duration
        );
        return new ResponseEntity<>(outputStream -> {
          try {
            byte[] byteArray = socketPath.getBytes(StandardCharsets.UTF_8);
            outputStream.write(byteArray);
            outputStream.close();
          } catch (Exception ex) {
            log.warn(ex.getMessage());
          }
        }, HttpStatus.OK);
      } catch (Exception ex) {
        final StreamingResponseBody streamingResponseBody = outputStream -> {
          outputStream.write(ex.getMessage().getBytes(StandardCharsets.UTF_8));
          outputStream.close();
        };
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(streamingResponseBody);
      }
    } else {
      log.info("INET Socket mode");
      return streamOperator.open(channelRemoteControl, duration, tunerStatus);
    }
  }

  // TODO: multiple player
  @DeleteMapping("{channelRemoteControl}/{tunerStatus}")
  public ResponseEntity<Object> stop(
      @PathVariable final int channelRemoteControl, final TunerStatus tunerStatus
  ) {
    return streamOperator.close(channelRemoteControl, tunerStatus);
  }

  // TODO: multiple player
  @DeleteMapping("{channelRemoteControl}")
  public ResponseEntity<Object> stop(@PathVariable final int channelRemoteControl) {
    log.info("[STOP] {}", channelRemoteControl);
    return streamOperator.close(channelRemoteControl, TunerStatus.LIVE);
  }
}

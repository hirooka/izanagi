package izanagi.domain.operator;

import static izanagi.constants.IzanagiConstants.FILE_SEPARATOR;
import static izanagi.constants.IzanagiConstants.MPEG2_TS_PACKET_LENGTH;
import static izanagi.constants.IzanagiConstants.UNIX_DOMAIN_SOCKET_EXTENSION;
import static izanagi.constants.IzanagiConstants.UNIX_DOMAIN_SOCKET_PREFIX;

import izanagi.domain.model.channel.ChannelConfiguration;
import izanagi.domain.model.stream.UnixDomainSocketInfo;
import izanagi.domain.model.tuner.Tuner;
import izanagi.domain.model.tuner.TunerStatus;
import izanagi.domain.model.tuner.TunerType;
import izanagi.domain.service.IUnixDomainSocketService;
import izanagi.domain.service.tuner.ITunerService;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.newsclub.net.unix.AFUNIXServerSocket;
import org.newsclub.net.unix.AFUNIXSocketAddress;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@AllArgsConstructor
@Service
public class UnixDomainSocketOperator implements IUnixDomainSocketOperator {

  private final IUnixDomainSocketService unixDomainSocketService;
  private final ITunerService tunerService;

  @Override
  public boolean isEnabled() {
    return unixDomainSocketService.isEnabled();
  }

  @Async
  @Override
  public void open(Tuner tuner, int channelRecording, String socketPath, long duration) {

    OutputStream outputStream = null;
    File file = null;
    Socket socket = null;
    try {

      final String channelString = Integer.toString(channelRecording);
      final String durationString;
      final TunerStatus tunerStatus;
      if (duration < 0) {
        durationString = "-";
        tunerStatus = TunerStatus.LIVE;
      } else {
        durationString = Long.toString(duration);
        tunerStatus = TunerStatus.RECORDING;
      }
      final String command = tuner.getCommand()
          .replace("<channel>", channelString)
          .replace("<duration>", durationString)
          .replace("<destination>", "-");
      final String[] commandArray = command.split(" ");

      //File file = new File(new File(System.getProperty("java.io.tmpdir")), socketName);
      file = new File(socketPath);
      if (file.exists()) {
        file.delete();
      }
      file = new File(socketPath);

      final AFUNIXServerSocket afunixServerSocket = AFUNIXServerSocket.newInstance();
      afunixServerSocket.bind(new AFUNIXSocketAddress(file));
      while (!Thread.interrupted()) {
        socket = afunixServerSocket.accept();

        log.info("Unix Domain Socket is accepted : {} {}, {}",
            tuner.getDeviceName(), channelRecording, socketPath);
        outputStream = socket.getOutputStream();

        final ProcessBuilder processBuilder = new ProcessBuilder(commandArray);
        final Process process = processBuilder.start();
        final long pid = process.pid();

        tunerService.lock(tuner, pid, channelRecording, tunerStatus, process);

        final byte[] buf = new byte[MPEG2_TS_PACKET_LENGTH];
        int len;
        while ((len = process.getInputStream().read(buf)) != -1) {
          outputStream.write(buf, 0, len);
        }
      }
    } catch (Exception e) {
      log.error(e.getMessage());
    } finally {
      if (tuner != null) {
        tunerService.release(tuner);
      }
      if (outputStream != null) {
        try {
          outputStream.close();
          log.info("outputStream is closed.");
        } catch (IOException e) {
          log.error(e.getMessage());
        }
      }
      if (socket != null) {
        try {
          socket.close();
          log.info("socket is closed.");
        } catch (IOException e) {
          log.error(e.getMessage());
        }
      }
      if (file != null) {
        if (file.exists()) {
          if (file.delete()) {
            log.info("{} is deleted.", file.getAbsolutePath());
          }
        }
      }
    }
  }

  @Override
  public UnixDomainSocketInfo getUnixDomainSocketInfo(
      int channelRemoteControl, long duration, TunerStatus tunerStatus
  ) throws Exception {

    final ChannelConfiguration channelConfiguration;
    try {
      channelConfiguration = tunerService.getChannelConfiguration(channelRemoteControl);
    } catch (Exception e) {
      throw new Exception(e.getMessage());
    }
    final TunerType tunerType = channelConfiguration.getType();
    final int channelRecording = channelConfiguration.getChannelRecording();

    if (tunerStatus == null) {
      if (duration < 0) {
        tunerStatus = TunerStatus.LIVE;
      } else {
        tunerStatus = TunerStatus.RECORDING;
      }
    }

    try {
      final Tuner tuner = tunerService.readSuitableOne(tunerType, tunerStatus);
      log.info("{}", tuner.toString());

      final String socketPath = unixDomainSocketService.getDirectory()
          + FILE_SEPARATOR
          + UNIX_DOMAIN_SOCKET_PREFIX
          + tunerType.name().toLowerCase()
          + "_"
          + tuner.getIndex()
          + "_" + channelRecording
          + UNIX_DOMAIN_SOCKET_EXTENSION;
      final UnixDomainSocketInfo unixDomainSocketInfo = new UnixDomainSocketInfo();
      unixDomainSocketInfo.setSocketPath(socketPath);
      unixDomainSocketInfo.setTuner(tuner);
      unixDomainSocketInfo.setChannelRecording(channelRecording);
      return unixDomainSocketInfo;
    } catch (Exception e) {
      throw new Exception(e.getMessage());
    }
  }
}

package izanagi.domain.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "epgdump")
public class EpgdumpConfiguration {
  private String epgdumpPath;
  private String epgdumpExecuteScheduleCron;
  private long epgdumpExecuteOnBootIgnoreInterval;
  String epgdumpTemporaryPath;
  int epgdumpRecordingDuration;
}

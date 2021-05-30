package izanagi.domain.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "unix-domain-socket")
public class UnixDomainSocketConfig {
  private boolean enabled;
  private String directory;
}

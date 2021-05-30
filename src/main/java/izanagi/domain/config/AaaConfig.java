package izanagi.domain.config;

import static izanagi.constants.IzanagiConstants.DEFAULT_PASSWORD;
import static izanagi.constants.IzanagiConstants.DEFAULT_USERNAME;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "aaa")
public class AaaConfig {

  private boolean enabled;
  private String initialUsername;
  private String initialPassword;

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public String getInitialUsername() {
    if (initialUsername.equals("")) {
      return DEFAULT_USERNAME;
    }
    return initialUsername;
  }

  public void setInitialUsername(String initialUsername) {
    this.initialUsername = initialUsername;
  }

  public String getInitialPassword() {
    if (initialPassword.equals("")) {
      return DEFAULT_PASSWORD;
    }
    return initialPassword;
  }

  public void setInitialPassword(String initialPassword) {
    this.initialPassword = initialPassword;
  }
}

package izanagi.domain.service;

import izanagi.domain.config.UnixDomainSocketConfig;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@AllArgsConstructor
@Service
public class UnixDomainSocketService implements IUnixDomainSocketService {

  private final UnixDomainSocketConfig unixDomainSocketConfig;

  @Override
  public boolean isEnabled() {
    return unixDomainSocketConfig.isEnabled();
  }

  @Override
  public String getDirectory() {
    return unixDomainSocketConfig.getDirectory();
  }
}

package izanagi.domain.service;

public interface IUnixDomainSocketService {
  boolean isEnabled();

  String getDirectory();
}

package izanagi.domain.service.user;

import izanagi.domain.entity.IzanagiUserDetailsEntity;
import java.util.List;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface IIzanagiUserDetailsService extends UserDetailsService {
  void createInitialUser();

  List<IzanagiUserDetailsEntity> readAllIzanagiUserDetailsEntity();
}

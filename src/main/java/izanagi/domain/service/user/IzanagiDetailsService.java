package izanagi.domain.service.user;

import izanagi.domain.config.AaaConfig;
import izanagi.domain.entity.IzanagiUserDetailsEntity;
import izanagi.domain.entity.IzanagiUserRoleEntity;
import izanagi.domain.repository.IIzanagiUserDetailsRepository;
import izanagi.domain.repository.IIzanagiUserRoleRepository;
import jakarta.annotation.PostConstruct;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@AllArgsConstructor
@Service
public class IzanagiDetailsService implements IIzanagiUserDetailsService {

  private final AaaConfig aaaConfig;
  private final IIzanagiUserDetailsRepository izanagiUserDetailsRepository;
  private final IIzanagiUserRoleRepository izanagiUserRoleRepository;

  @PostConstruct
  void init() {
    if (readAllIzanagiUserDetailsEntity().size() == 0) {
      createInitialUser();
    }
  }

  @Override
  public void createInitialUser() {

    log.info("CREATE INITIAL USER");

    IzanagiUserRoleEntity adminIzanagiUserRoleEntity = new IzanagiUserRoleEntity();
    adminIzanagiUserRoleEntity.setName("ADMIN");
    adminIzanagiUserRoleEntity.setAuthority("ROLE_ADMIN");
    izanagiUserRoleRepository.save(adminIzanagiUserRoleEntity);

    IzanagiUserRoleEntity guestIzanagiUserRoleEntity = new IzanagiUserRoleEntity();
    guestIzanagiUserRoleEntity.setName("GUEST");
    guestIzanagiUserRoleEntity.setAuthority("ROLE_GUEST");
    izanagiUserRoleRepository.save(guestIzanagiUserRoleEntity);

    Set<IzanagiUserRoleEntity> izanagiUserRoleEntitySet = new HashSet<>();
    izanagiUserRoleEntitySet.add(adminIzanagiUserRoleEntity);
    izanagiUserRoleEntitySet.add(guestIzanagiUserRoleEntity);

    PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    IzanagiUserDetailsEntity izanagiUserDetailsEntity = new IzanagiUserDetailsEntity();
    izanagiUserDetailsEntity.setUsername(aaaConfig.getInitialUsername());
    izanagiUserDetailsEntity.setPassword(passwordEncoder.encode(aaaConfig.getInitialPassword()));
    izanagiUserDetailsEntity.setIzanagiUserRoleEntitySet(izanagiUserRoleEntitySet);
    izanagiUserDetailsRepository.save(izanagiUserDetailsEntity);
  }

  @Override
  public List<IzanagiUserDetailsEntity> readAllIzanagiUserDetailsEntity() {
    return izanagiUserDetailsRepository.findAll();
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return izanagiUserDetailsRepository.findOneByUsername(username);
  }
}

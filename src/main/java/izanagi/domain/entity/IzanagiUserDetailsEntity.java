package izanagi.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
public class IzanagiUserDetailsEntity implements UserDetails {

  @Id
  private UUID uuid = UUID.randomUUID();

  @Column(unique = true)
  private String username;
  private String password;

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(name = "izanagi_user_role")
  private Set<IzanagiUserRoleEntity> izanagiUserRoleEntitySet;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    final Set<String> izanagiUserRoleAuthoritySet = izanagiUserRoleEntitySet.stream()
        .map(IzanagiUserRoleEntity::getAuthority)
        .collect(Collectors.toSet());
    return AuthorityUtils.createAuthorityList(izanagiUserRoleAuthoritySet.toArray(new String[0]));
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return username;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  public UUID getUuid() {
    return uuid;
  }

  public void setUuid(UUID uuid) {
    this.uuid = uuid;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public Set<IzanagiUserRoleEntity> getIzanagiUserRoleEntitySet() {
    return izanagiUserRoleEntitySet;
  }

  public void setIzanagiUserRoleEntitySet(Set<IzanagiUserRoleEntity> izanagiUserRoleEntitySet) {
    this.izanagiUserRoleEntitySet = izanagiUserRoleEntitySet;
  }
}

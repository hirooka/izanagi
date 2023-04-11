package izanagi.domain.config;

import static java.util.Objects.requireNonNull;

import izanagi.domain.service.user.IIzanagiUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@EnableWebSecurity
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class MultiHttpSecurityConfiguration {

  @Configuration
  @Order(1)
  public static class ApiWebSecurityConfigurationAdapter {

    private final AaaConfig aaaConfig;
    private final IIzanagiUserDetailsService izanagiUserDetailsService;

    @Autowired
    public ApiWebSecurityConfigurationAdapter(
        AaaConfig aaaConfig,
        IIzanagiUserDetailsService izanagiUserDetailsService
    ) {
      this.aaaConfig = requireNonNull(aaaConfig);
      this.izanagiUserDetailsService = requireNonNull(izanagiUserDetailsService);
    }

    @Bean
    public SecurityFilterChain securityFilterChain10(HttpSecurity http) throws Exception {
      if (aaaConfig.isEnabled()) {
        http
                .authorizeHttpRequests(authz ->
                        authz.requestMatchers("/api/**")
                                .permitAll().anyRequest().authenticated())
                .httpBasic()
                .and()
                .csrf()
                .disable();
      } else {
        http
                .authorizeHttpRequests(authz -> authz.requestMatchers("/**").permitAll())
                .csrf().disable()
                .httpBasic().disable();
      }
      return http.build();
    }

    @Bean
    public SecurityFilterChain securityFilterChain11(HttpSecurity http) throws Exception {
      http.authorizeHttpRequests(authz ->
              authz.requestMatchers("/images/**", "/webjars/**").permitAll());
      return http.build();
    }
  }

  @Configuration
  public static class FormLoginWebSecurityConfigurerAdapter {

    final AaaConfig aaaConfig;
    final IIzanagiUserDetailsService izanagiUserDetailsService;

    @Autowired
    public FormLoginWebSecurityConfigurerAdapter(
        AaaConfig aaaConfig,
        IIzanagiUserDetailsService izanagiUserDetailsService
    ) {
      this.aaaConfig = requireNonNull(aaaConfig);
      this.izanagiUserDetailsService = requireNonNull(izanagiUserDetailsService);
    }

    @Bean
    public SecurityFilterChain securityFilterChain20(HttpSecurity http) throws Exception {
      if (aaaConfig.isEnabled()) {
        http
            .authorizeRequests()
            .anyRequest()
            .authenticated()
            .and()
            .formLogin()
            .loginPage("/login")
            .defaultSuccessUrl("/izanagi")
            .permitAll()
            .and()
            .logout()
            .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
            .logoutSuccessUrl("/login")
            .deleteCookies("JSESSIONID")
            .invalidateHttpSession(true)
            .permitAll();
      } else {
        http
            .authorizeHttpRequests(authz -> authz.requestMatchers("/**").permitAll())
            .csrf().disable()
            .httpBasic().disable();
      }
      return http.build();
    }

    @Bean
    public SecurityFilterChain securityFilterChain21(HttpSecurity http) throws Exception {
      http.authorizeHttpRequests(authz ->
              authz.requestMatchers("/images/**", "/webjars/**").permitAll());
      return http.build();
    }
  }
}

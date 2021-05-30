package izanagi.domain.config;

import static java.util.Objects.requireNonNull;

import izanagi.domain.service.user.IIzanagiUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@EnableWebSecurity
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class MultiHttpSecurityConfiguration {

  @Configuration
  @Order(1)
  public static class ApiWebSecurityConfigurationAdapter extends WebSecurityConfigurerAdapter {

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

    @Override
    protected void configure(
        AuthenticationManagerBuilder authenticationManagerBuilder
    ) throws Exception {
      if (aaaConfig.isEnabled()) {
        authenticationManagerBuilder.userDetailsService(this.izanagiUserDetailsService);
      }
    }

    protected void configure(HttpSecurity http) throws Exception {
      if (aaaConfig.isEnabled()) {
        http
            .antMatcher("/api/**")
            .authorizeRequests()
            .anyRequest()
            .authenticated()
            .and()
            .httpBasic()
            .and()
            .csrf()
            .disable();
      } else {
        http
            .antMatcher("/**")
            .authorizeRequests()
            .anyRequest()
            .permitAll()
            .and()
            .csrf().disable()
            .httpBasic().disable();
      }
    }

    @Override
    public void configure(WebSecurity web) {
      web
          .ignoring()
          .antMatchers("/images/**", "/webjars/**");
    }
  }

  @Configuration
  public static class FormLoginWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

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

    @Override
    protected void configure(
        AuthenticationManagerBuilder authenticationManagerBuilder
    ) throws Exception {
      if (aaaConfig.isEnabled()) {
        authenticationManagerBuilder.userDetailsService(this.izanagiUserDetailsService);
      }
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
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
            .antMatcher("/**")
            .authorizeRequests()
            .anyRequest()
            .permitAll()
            .and()
            .csrf().disable()
            .httpBasic().disable();
      }
    }

    @Override
    public void configure(WebSecurity web) {
      web
          .ignoring()
          .antMatchers("/images/**", "/webjars/**");
    }
  }
}

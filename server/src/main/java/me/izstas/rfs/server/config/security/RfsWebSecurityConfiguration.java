package me.izstas.rfs.server.config.security;

import java.util.Collections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.vote.RoleVoter;
import org.springframework.security.access.vote.UnanimousBased;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.context.request.async.WebAsyncManagerIntegrationFilter;

@Configuration
@EnableWebMvcSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class RfsWebSecurityConfiguration extends WebSecurityConfigurerAdapter {
    @Autowired
    private RfsUserService userService;

    @Value("${rfs.security.realm:RFS}")
    private String httpBasicAuthRealm;

    public RfsWebSecurityConfiguration() {
        super(true);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(new BCryptPasswordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.addFilter(new WebAsyncManagerIntegrationFilter())
                .exceptionHandling().and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .securityContext().and()
                .servletApi().and()
                .httpBasic().realmName(httpBasicAuthRealm).and()
                .apply(new RfsAnonymousConfigurer<HttpSecurity>());
    }

    @Bean // Exposing our AuthenticationManager as a bean prevents Spring Boot auto-configuration from constructing the default one
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public AccessDecisionManager accessDecisionManagerBean() {
        RoleVoter roleVoter = new RoleVoter();
        roleVoter.setRolePrefix("");

        return new UnanimousBased(Collections.<AccessDecisionVoter>singletonList(roleVoter));
    }
}

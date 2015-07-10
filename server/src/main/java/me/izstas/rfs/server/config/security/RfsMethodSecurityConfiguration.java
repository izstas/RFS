package me.izstas.rfs.server.config.security;

import java.util.Collections;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.vote.RoleVoter;
import org.springframework.security.access.vote.UnanimousBased;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;

@Configuration
@EnableGlobalMethodSecurity(securedEnabled = true)
public class RfsMethodSecurityConfiguration extends GlobalMethodSecurityConfiguration {
    @Override
    protected AccessDecisionManager accessDecisionManager() {
        RoleVoter roleVoter = new RoleVoter();
        roleVoter.setRolePrefix("");

        return new UnanimousBased(Collections.<AccessDecisionVoter>singletonList(roleVoter));
    }
}

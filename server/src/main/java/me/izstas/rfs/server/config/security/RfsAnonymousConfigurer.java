package me.izstas.rfs.server.config.security;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;

/**
 * Configures anonymous authentication.
 *
 * We use a custom implementation rather than using the Spring's one
 * ({@link org.springframework.security.config.annotation.web.configurers.AnonymousConfigurer})
 * because we want to have our anonymous authentication token (which, in our implementation, is a regular
 * {@link org.springframework.security.authentication.UsernamePasswordAuthenticationToken})
 * to be authenticated and populated by the {@link org.springframework.security.authentication.AuthenticationManager}.
 */
final class RfsAnonymousConfigurer<B extends HttpSecurityBuilder<B>>
        extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, B> {

    private String principal = "anonymous";
    private String credentials = "";

    public RfsAnonymousConfigurer<B> principal(String principal) {
        this.principal = principal;
        return this;
    }

    public RfsAnonymousConfigurer<B> credentials(String credentials) {
        this.credentials = credentials;
        return this;
    }


    @Override
    public void configure(B http) throws Exception {
        AuthenticationManager authManager = http.getSharedObject(AuthenticationManager.class);

        RfsAnonymousAuthenticationFilter authFilter = new RfsAnonymousAuthenticationFilter(authManager, principal, credentials);
        authFilter = postProcess(authFilter);
        http.addFilterAfter(authFilter, AnonymousAuthenticationFilter.class);
    }
}

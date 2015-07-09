package me.izstas.rfs.server.config.security;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

/**
 * @see me.izstas.rfs.server.config.security.RfsAnonymousConfigurer
 */
final class RfsAnonymousAuthenticationFilter extends GenericFilterBean {
    private final AuthenticationManager authManager;

    private final Object principal;
    private final Object credentials;

    public RfsAnonymousAuthenticationFilter(AuthenticationManager authManager, Object principal, Object credentials) {
        this.authManager = authManager;
        this.principal = principal;
        this.credentials = credentials;
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(principal, credentials);

            try {
                Authentication auth = authManager.authenticate(authToken);

                SecurityContextHolder.getContext().setAuthentication(auth);
                logger.debug("Successfully authenticated anonymous token and populated SecurityContext");
            }
            catch (AuthenticationException e) {
                SecurityContextHolder.clearContext();
                logger.debug("Failed to authenticate anonymous token and cleared SecurityContext", e);
            }
        }

        chain.doFilter(req, res);
    }
}

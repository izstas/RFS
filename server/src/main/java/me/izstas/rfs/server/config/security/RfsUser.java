package me.izstas.rfs.server.config.security;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class RfsUser implements RfsUserDetails {
    private String username;
    private String password;
    private Path root;
    private List<String> access = new ArrayList<>();

    /* Methods necessary for configuration binding */
    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRoot(String root) {
        this.root = Paths.get(root);
    }

    public List<String> getAccess() {
        return access;
    }

    /* Methods implementing RfsUserDetails */
    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Path getRoot() {
        return root;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (String authority : access) {
            authorities.add(new SimpleGrantedAuthority(authority));
        }

        return Collections.unmodifiableCollection(authorities);
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /* Safe wrapper without setters */
    public RfsUserDetails wrap() {
        return new Wrapper();
    }

    private final class Wrapper implements RfsUserDetails, CredentialsContainer {
        private boolean erasedCredentials;

        @Override
        public String getUsername() {
            return RfsUser.this.getUsername();
        }

        @Override
        public String getPassword() {
            return !erasedCredentials ? RfsUser.this.getPassword() : null;
        }

        @Override
        public Path getRoot() {
            return RfsUser.this.getRoot();
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return RfsUser.this.getAuthorities();
        }

        @Override
        public boolean isEnabled() {
            return RfsUser.this.isEnabled();
        }

        @Override
        public boolean isAccountNonLocked() {
            return RfsUser.this.isAccountNonLocked();
        }

        @Override
        public boolean isAccountNonExpired() {
            return RfsUser.this.isAccountNonExpired();
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return RfsUser.this.isCredentialsNonExpired();
        }

        @Override
        public void eraseCredentials() {
            erasedCredentials = true;
        }
    }
}

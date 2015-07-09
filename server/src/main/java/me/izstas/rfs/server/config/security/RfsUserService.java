package me.izstas.rfs.server.config.security;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@ConfigurationProperties("rfs.security")
public class RfsUserService implements UserDetailsService {
    private List<RfsUser> users = new ArrayList<>();
    private Map<String, RfsUser> usersMap = new HashMap<>();

    // Necessary for configuration binding
    public List<RfsUser> getUsers() {
        return users;
    }

    @PostConstruct
    public void initializeUsers() {
        usersMap.clear();

        for (RfsUser user : users) {
            usersMap.put(user.getUsername(), user);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        RfsUser user = usersMap.get(username);
        if (user == null) {
            throw new UsernameNotFoundException("User " + username + " doesn't exist");
        }
        if (user.getAuthorities().isEmpty()) {
            throw new UsernameNotFoundException("User " + username + " doesn't have any granted authorities");
        }

        return user.wrap();
    }
}

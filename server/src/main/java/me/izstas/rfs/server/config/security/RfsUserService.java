package me.izstas.rfs.server.config.security;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@ConfigurationProperties("rfs.security")
public class RfsUserService implements UserDetailsService, InitializingBean {
    private static final Log logger = LogFactory.getLog(RfsUserService.class);

    private final List<RfsUser> users = new ArrayList<>();
    private final Map<String, RfsUser> usersMap = new HashMap<>();

    // Necessary for configuration binding
    public List<RfsUser> getUsers() {
        return users;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        for (RfsUser user : users) {
            usersMap.put(user.getUsername(), user);
        }

        logger.info("Loaded " + usersMap.size() + " users: " + usersMap.keySet());
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

package me.izstas.rfs.server.config.security;

import java.nio.file.Path;
import org.springframework.security.core.userdetails.UserDetails;

public interface RfsUserDetails extends UserDetails {
    Path getRoot();
}

package com.kostya.filesDump.repositories.interfaces;

import com.kostya.filesDump.entities.User;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * Created by Костя on 14.05.2017.
 */
public interface UserRepository extends UserDetailsService {
    User getUserById(Long userId);

    User getUserByEmail(String email);

    void putUser(User user);
}

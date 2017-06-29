package com.kostya.filesDump.utils;

import com.kostya.filesDump.entities.User;
import com.kostya.filesDump.exceptions.NoSuchFileException;
import com.kostya.filesDump.repositories.interfaces.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.regex.Pattern;

/**
 * Created by Костя on 26.06.2017.
 */

@Component
public class ControllersUtils {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FileResolver fileResolver;

    public boolean isValidUserCredentials(String email, String password){
        User user = userRepository.getUserByEmail(email);

        if(user == null){
            return false;
        }else if(!user.getPassword().equals(password)){
            return false;
        }

        return true;
    }

    public String getRelationalResourcePath(HttpServletRequest request, String prefix){
        String relationalFilePath = request.getServletPath();
        relationalFilePath = relationalFilePath.replaceFirst(Pattern.quote(prefix), "");
        return relationalFilePath.replaceAll("//", File.separator);
    }


}

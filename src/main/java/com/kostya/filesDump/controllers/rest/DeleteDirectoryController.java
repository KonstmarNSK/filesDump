package com.kostya.filesDump.controllers.rest;

import com.kostya.filesDump.repositories.interfaces.UserRepository;
import com.kostya.filesDump.utils.ControllersUtils;
import com.kostya.filesDump.utils.FileResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * Created by Костя on 26.06.2017.
 */
@Controller
public class DeleteDirectoryController {
    @Autowired
    FileResolver fileResolver;

    @Autowired
    ControllersUtils contollersUtils;

    @DeleteMapping("/rest/userDirectory/{userPassword}/{userEmail}/**")
    public  void deleteDirectory(HttpServletRequest request, @PathVariable("userPassword") String rawUserPassword, @PathVariable("userEmail") String rawUsername) throws UnsupportedEncodingException{
        String username = URLDecoder.decode(rawUsername, "UTF-8");
        String password = URLDecoder.decode(rawUserPassword, "UTF-8");

        if(!contollersUtils.isValidUserCredentials(username, password)){
            return;
        }

        String rawRelationalFilePath = contollersUtils.getRelationalResourcePath(request, "/rest/userDirectory/"+rawUserPassword);
        String relationalPath = URLDecoder.decode(rawRelationalFilePath, "UTF-8");
        File requestedFile = fileResolver.getFile(relationalPath);

        requestedFile.delete();
    }
}

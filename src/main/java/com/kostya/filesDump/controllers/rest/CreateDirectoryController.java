package com.kostya.filesDump.controllers.rest;

import com.kostya.filesDump.utils.ControllersUtils;
import com.kostya.filesDump.utils.FileResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;

/**
 * Created by Костя on 26.06.2017.
 */
@Controller
public class CreateDirectoryController {
    @Autowired
    FileResolver fileResolver;

    @Autowired
    ControllersUtils contollersUtils;

    @PostMapping("/rest/userDirectory/{userPassword}/{userEmail}/**")
    public void createDirectory(HttpServletRequest request, @PathVariable("userPassword") String rawUserPassword, @PathVariable("userEmail") String rawUsername) throws IOException {
        String username = URLDecoder.decode(rawUsername, "UTF-8");
        String password = URLDecoder.decode(rawUserPassword, "UTF-8");
        System.out.println(username + "; " + password);

        if(!contollersUtils.isValidUserCredentials(username, password)){
            return;
        }
        System.out.println("exists");

        String rawRelationalFilePath = contollersUtils.getRelationalResourcePath(request, "/rest/userDirectory/"+rawUserPassword);
        System.out.println("raw path: "+rawRelationalFilePath);
        String relationalPath = URLDecoder.decode(rawRelationalFilePath, "UTF-8");
        System.out.println("relational path: "+relationalPath);
        File requestedFile = fileResolver.getFile(relationalPath);

        System.out.println("requestedFile: "+requestedFile.getAbsolutePath());
        if(!requestedFile.getParentFile().exists()){
            return;
        }

        requestedFile.mkdir();
    }
}

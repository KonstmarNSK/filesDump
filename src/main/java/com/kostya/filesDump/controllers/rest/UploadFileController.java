package com.kostya.filesDump.controllers.rest;

import com.kostya.filesDump.repositories.interfaces.UserRepository;
import com.kostya.filesDump.utils.ControllersUtils;
import com.kostya.filesDump.utils.FileResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;

/**
 * Created by Костя on 26.06.2017.
 */
@Controller
public class UploadFileController {
    @Autowired
    FileResolver fileResolver;

    @Autowired
    ControllersUtils contollersUtils;

    @PostMapping("/rest/userFile/{userPassword}/{userEmail}/**")
    public void postFile(HttpServletRequest request, @PathVariable("userPassword") String rawUserPassword, @PathVariable("userEmail") String rawUsername, @RequestParam("file") MultipartFile multipartFile) throws IOException {
        String username = URLDecoder.decode(rawUsername, "UTF-8");
        String password = URLDecoder.decode(rawUserPassword, "UTF-8");

        if(!contollersUtils.isValidUserCredentials(username, password)){
            return;
        }

        String rawRelationalFilePath = contollersUtils.getRelationalResourcePath(request, "/rest/userFile/"+rawUserPassword);
        String relationalPath = URLDecoder.decode(rawRelationalFilePath, "UTF-8");
        File requestedFile = fileResolver.getFile(relationalPath);

        if(!requestedFile.getParentFile().exists()){
            return;
        }

        if(!requestedFile.exists()){
            requestedFile.createNewFile();
        }

        multipartFile.transferTo(requestedFile);
    }
}

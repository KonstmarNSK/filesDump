package com.kostya.filesDump.controllers.rest;

import com.kostya.filesDump.exceptions.NoSuchUserException;
import com.kostya.filesDump.repositories.interfaces.UserRepository;
import com.kostya.filesDump.utils.ControllersUtils;
import com.kostya.filesDump.utils.FileResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Костя on 26.06.2017.
 */
@Controller
public class GetDirectoryFilesListController {
    @Autowired
    FileResolver fileResolver;

    @Autowired
    ServletContext servletContext;

    @Autowired
    ControllersUtils contollersUtils;

    private long getDirectorySize(File file){
        long result = 0L;
        try{
            result = Arrays.stream(file.listFiles()).reduce(result, (partialSize, anotherFile)->partialSize + ((anotherFile.isDirectory())?getDirectorySize(anotherFile):anotherFile.length()), (a, b)->a+b);
        }catch (NullPointerException e){
            //nop, it's ok; returning 0: directory is empty
        }

        return result;
    }

    private class FileInfo{
        public final String filename;
        public final long createTime;
        public final long lastModified;
        public final boolean isDirectory;
        public final long fileSize;
        public final String fileType;

        public FileInfo(File file){
            String mimeType = servletContext.getMimeType(file.getAbsolutePath());
            if(mimeType == null){
                fileType = "unknown";
            }else {
                fileType = mimeType;
            }

            if(file.isDirectory()){
                isDirectory = true;
                fileSize = getDirectorySize(file);
            }else {
                this.fileSize = file.length();
                isDirectory = false;
            }
            this.lastModified = file.lastModified();
            this.filename = file.getName();
            Path path = Paths.get(file.getAbsolutePath());

            BasicFileAttributes view = null;
            try {
                view = Files.getFileAttributeView(path, BasicFileAttributeView.class).readAttributes();
            }catch (IOException e){
                throw new RuntimeException(e);
            }
            createTime = view.creationTime().toMillis();
        }
    }


    @GetMapping("/rest/userDirectory/{userPassword}/{userEmail}/**")
    @ResponseBody
    public List<FileInfo> getDirectoryFilesList(HttpServletRequest request, @PathVariable("userPassword") String rawUserPassword, @PathVariable("userEmail") String rawUsername) throws IOException{
        String username = URLDecoder.decode(rawUsername, "UTF-8");
        String password = URLDecoder.decode(rawUserPassword, "UTF-8");

        System.out.println(username+" "+password);
        if(!contollersUtils.isValidUserCredentials(username, password)){
            throw new NoSuchUserException();
        }
        System.out.println("exists");
        String rawRelationalFilePath = contollersUtils.getRelationalResourcePath(request, "/rest/userDirectory/"+rawUserPassword);
        String relationalPath = URLDecoder.decode(rawRelationalFilePath, "UTF-8");
        File requestedFile = fileResolver.getFile(relationalPath);

        System.out.println(requestedFile.getAbsolutePath());
        if(requestedFile.isDirectory()){
            File[] files = requestedFile.listFiles();
            if(files == null){
                return new ArrayList<FileInfo>(0);
            }
            return Arrays.stream(files).map(FileInfo::new).collect(Collectors.toList());
        }else{
            return null;
        }
    }
}

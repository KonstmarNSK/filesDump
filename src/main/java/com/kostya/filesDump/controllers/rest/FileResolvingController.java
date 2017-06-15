package com.kostya.filesDump.controllers.rest;

import com.kostya.filesDump.entities.User;
import com.kostya.filesDump.repositories.interfaces.UserRepository;
import com.kostya.filesDump.utils.FileResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by Костя on 01.06.2017.
 */

@Controller
public class FileResolvingController {
    @Autowired
    FileResolver fileResolver;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ServletContext servletContext;

    @ExceptionHandler(IOException.class)
    public String returnNotFoundPage(){
        return "notFound";
    }

    // TODO: 05.06.2017 find out something about urlencoded values 
    // TODO: 05.06.2017 add delete actions 
    // TODO: 05.06.2017 make server answer more informative
    // TODO: 05.06.2017 add web interface (upload, download, delete, getFilesList etc)
    // TODO: 05.06.2017 fix tests (they must not depend on filesystem)

    @GetMapping("/rest/userFile/{userPassword}/{userEmail}/**")
    public void getFile(HttpServletRequest request, HttpServletResponse response, @PathVariable("userPassword") String userPassword, @PathVariable("userEmail") String username) throws IOException{
        if(!isValidUserCredentials(username, userPassword)){
            return;
        }

        String relationalFilePath = getRelationalResourcePath(request, "/rest/userFile/"+userPassword);
        File requestedFile = fileResolver.getFile(relationalFilePath);

        if(!requestedFile.isDirectory()){
            returnFile(response, requestedFile);
        }
    }

    @PostMapping("/rest/userFile/{userPassword}/{userEmail}/**")
    public void postFile(HttpServletRequest request, @PathVariable("userPassword") String userPassword, @PathVariable("userEmail") String username, @RequestParam("file") MultipartFile multipartFile) throws IOException{
        if(!isValidUserCredentials(username, userPassword)){
            return;
        }

        String relationalFilePath = getRelationalResourcePath(request, "/rest/userFile/"+userPassword);
        File requestedFile = fileResolver.getFile(relationalFilePath);


        if(!requestedFile.getParentFile().exists()){
            return;
        }

        multipartFile.transferTo(requestedFile);
    }

    private class FileInfo{
        public final String filename;
        public final Date createTime;
        public final boolean isDirectory;

        public FileInfo(File file){
            this.filename = file.getName();
            Path path = Paths.get(file.getAbsolutePath());
            isDirectory = file.isDirectory();

            BasicFileAttributes view = null;
            try {
                view = Files.getFileAttributeView(path, BasicFileAttributeView.class).readAttributes();
            }catch (IOException e){
                throw new RuntimeException(e);
            }
            createTime = new Date(view.creationTime().toMillis());
        }
    }

    @GetMapping("/rest/userDirectory/{userPassword}/{userEmail}/**")
    @ResponseBody
    public List<FileInfo> getDirectoryFilesList(HttpServletRequest request, @PathVariable("userPassword") String userPassword, @PathVariable("userEmail") String username) throws IOException{
        if(!isValidUserCredentials(username, userPassword)){
            return null;
        }

        String relationalFilePath = getRelationalResourcePath(request, "/rest/userDirectory/"+userPassword);
        File requestedFile = fileResolver.getFile(relationalFilePath);

        if(requestedFile.isDirectory()){
            File[] files = requestedFile.listFiles();
            if(files == null){
                return null;
            }
            return Arrays.stream(files).map(FileInfo::new).collect(Collectors.toList());
        }else{
            return null;
        }
    }

    @PostMapping("/rest/userDirectory/{userPassword}/{userEmail}/**")
    public void createDirectory(HttpServletRequest request, @PathVariable("userPassword") String userPassword, @PathVariable("userEmail") String username, @RequestParam("directoryName") String directoryName) throws IOException{
        if(!isValidUserCredentials(username, userPassword)){
            return;
        }

        String relationalFilePath = getRelationalResourcePath(request, "/rest/userDirectory/"+userPassword);
        File requestedFile = fileResolver.getFile(relationalFilePath);

        if(!requestedFile.getParentFile().exists()){
            return;
        }

        requestedFile.mkdir();
    }


    private void returnFile(HttpServletResponse response, File file) throws IOException{
        String contentType = servletContext.getMimeType(file.getAbsolutePath());
        if(contentType == null){
            contentType="application/octet-stream";
        }

        response.setContentType(contentType);
        response.setHeader("Content-disposition", "attachment; filename=\""+file.getName()+"\"");
        response.setContentLength((int)file.length());

        ServletOutputStream outputStream = response.getOutputStream();
        InputStream in = new FileInputStream(file);
        int bytesCount;

        byte[] buffer = new byte[1024];

        while((bytesCount = in.read(buffer)) != -1){
            outputStream.write(buffer, 0, bytesCount);
        }

        in.close();
        outputStream.close();
    }

    private boolean isValidUserCredentials(String email, String password){
        User user = userRepository.getUserByEmail(email);

        if(user == null){
            return false;
        }else if(!user.getPassword().equals(password)){
            return false;
        }

        return true;
    }

    private String getRelationalResourcePath(HttpServletRequest request, String prefix){
        String relationalFilePath = request.getServletPath();
        relationalFilePath = relationalFilePath.replaceFirst(Pattern.quote(prefix), "");
        return relationalFilePath.replaceAll("//", File.separator);
    }
}
package com.kostya.filesDump.controllers.rest;

import com.kostya.filesDump.exceptions.NoSuchFileException;
import com.kostya.filesDump.utils.ControllersUtils;
import com.kostya.filesDump.utils.FileResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLDecoder;

/**
 * Created by Костя on 26.06.2017.
 */
@Controller
@RequestMapping("/rest/userFile/{userPassword}/{userEmail}/**")
public class FileController {
    @Autowired
    FileResolver fileResolver;

    @Autowired
    ServletContext servletContext;

    @Autowired
    ControllersUtils contollersUtils;

    @GetMapping
    public void getFile(HttpServletRequest request, HttpServletResponse response, @PathVariable("userPassword") String rawUserPassword, @PathVariable("userEmail") String rawUsername) throws IOException {
        System.out.println("raw servlet path: "+request.getServletPath());
        System.out.println("servlet path: "+URLDecoder.decode(request.getServletPath(), "UTF-8"));
        System.out.println("password: "+URLDecoder.decode(rawUserPassword, "UTF-8"));
        System.out.println("username: "+URLDecoder.decode(rawUsername, "UTF-8"));

        String username = URLDecoder.decode(rawUsername, "UTF-8");
        String password = URLDecoder.decode(rawUserPassword, "UTF-8");

        if(!contollersUtils.isValidUserCredentials(username, password)){
            return;
        }


        String rawRelationalFilePath = contollersUtils.getRelationalResourcePath(request, "/rest/userFile/"+rawUserPassword);
        String relationalPath = URLDecoder.decode(rawRelationalFilePath, "UTF-8");

        System.out.println("Relational path: "+relationalPath);
        File requestedFile = fileResolver.getFile(relationalPath);


        if(!requestedFile.exists()){
            System.out.println("File \""+requestedFile.getAbsolutePath()+"\" doesn't exist");
            throw new NoSuchFileException(requestedFile.getAbsolutePath());
        }
        System.out.println("File \""+requestedFile.getAbsolutePath()+"\" exists");

        if(!requestedFile.isDirectory()){
            returnFile(response, requestedFile);
        }
    }

    @PostMapping
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

    @DeleteMapping
    public  void deleteFile(HttpServletRequest request, @PathVariable("userPassword") String rawUserPassword, @PathVariable("userEmail") String rawUsername) throws UnsupportedEncodingException {
        String username = URLDecoder.decode(rawUsername, "UTF-8");
        String password = URLDecoder.decode(rawUserPassword, "UTF-8");

        if(!contollersUtils.isValidUserCredentials(username, password)){
            return;
        }

        String rawRelationalFilePath = contollersUtils.getRelationalResourcePath(request, "/rest/userFile/"+rawUserPassword);
        String relationalPath = URLDecoder.decode(rawRelationalFilePath, "UTF-8");
        File requestedFile = fileResolver.getFile(relationalPath);

        requestedFile.delete();
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
}

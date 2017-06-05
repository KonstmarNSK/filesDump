package com.kostya.filesDump.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * Created by Костя on 01.06.2017.
 */
@Component
@PropertySource("classpath:/properties/global.properties")
public class FileResolver {
    @Autowired
    Environment environment;

    private String getAbsolutePath(String relationalPath){

        String absoluteFilePath = environment.getProperty("baseDirectoryPath");
        if(absoluteFilePath.endsWith(File.separator)){
            absoluteFilePath = absoluteFilePath.substring(0, absoluteFilePath.lastIndexOf(File.separator));
        }
        if(!relationalPath.startsWith(File.separator)){
            relationalPath = File.separator+relationalPath;
        }

        absoluteFilePath += relationalPath;

        return absoluteFilePath;
    }

    public File getFile(String relationalPath){
        String absolutePath = getAbsolutePath(relationalPath);
        File result = new File(absolutePath);
        return result;
    }

    public File createUserRootDirectory(String username){
        String userRootDirectoryPath = environment.getProperty("baseDirectoryPath");
        if(!userRootDirectoryPath.endsWith(File.separator)){
            userRootDirectoryPath += File.separator;
        }

        userRootDirectoryPath += username;

        File userRootDirectory = new File(userRootDirectoryPath);
        if(userRootDirectory.exists()){
            throw new IllegalArgumentException("Directory "+userRootDirectory.getPath()+" already exists");
        }

        userRootDirectory.mkdir();
        return userRootDirectory;
    }


}

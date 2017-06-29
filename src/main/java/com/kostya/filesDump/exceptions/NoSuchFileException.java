package com.kostya.filesDump.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by Костя on 26.06.2017.
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND,reason = "no such file")
public class NoSuchFileException extends RuntimeException {
    public NoSuchFileException(String filePath){
        super(filePath);
    }
}

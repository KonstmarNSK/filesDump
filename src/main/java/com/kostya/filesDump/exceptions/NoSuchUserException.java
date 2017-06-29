package com.kostya.filesDump.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by Костя on 28.06.2017.
 */
@ResponseStatus(value = HttpStatus.FORBIDDEN, reason = "no such user")
public class NoSuchUserException extends RuntimeException {
}

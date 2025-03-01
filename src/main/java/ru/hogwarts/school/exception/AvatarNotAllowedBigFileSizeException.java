package ru.hogwarts.school.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class AvatarNotAllowedBigFileSizeException extends RuntimeException {
    public AvatarNotAllowedBigFileSizeException(long size) {
        super("Illegal size of avatar file: %s".formatted(size) + " it must be no longer than " + 1024 * 300);
    }
}

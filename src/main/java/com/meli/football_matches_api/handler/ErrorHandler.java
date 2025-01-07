package com.meli.football_matches_api.handler;

import com.meli.football_matches_api.DTO.ErrorDTO;
import com.meli.football_matches_api.exception.ConflictException;
import com.meli.football_matches_api.exception.FieldException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ErrorHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    @ExceptionHandler(FieldException.class)
    public ErrorDTO handler(FieldException ex) {
        return new ErrorDTO(
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value()
        );
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    @ExceptionHandler(ConflictException.class)
    public ErrorDTO handler(ConflictException ex) {
        return new ErrorDTO(
                ex.getMessage(),
                HttpStatus.CONFLICT.value()
        );
    }
}

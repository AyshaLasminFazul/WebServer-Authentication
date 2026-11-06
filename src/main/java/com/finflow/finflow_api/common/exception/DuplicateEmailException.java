package com.finflow.finflow_api.common.exception;

import org.springframework.web.bind.annotation.RestControllerAdvice;


public class DuplicateEmailException extends RuntimeException{

    public  DuplicateEmailException( String message ){
            super(message);
    }
}

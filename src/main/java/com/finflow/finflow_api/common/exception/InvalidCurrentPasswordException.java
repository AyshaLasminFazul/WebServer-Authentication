package com.finflow.finflow_api.common.exception;

public class InvalidCurrentPasswordException extends RuntimeException{

    public InvalidCurrentPasswordException(String message){
        super(message);
    }
}

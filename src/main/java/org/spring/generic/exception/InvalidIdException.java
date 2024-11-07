package org.spring.generic.exception;

public class InvalidIdException extends RuntimeException {

    public InvalidIdException(String message){
        super(message);
    }
}

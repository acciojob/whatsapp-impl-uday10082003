package com.driver;

public class UserAlreadyExistsException extends Throwable {
    public UserAlreadyExistsException(String message){
        super(message);
    }
}

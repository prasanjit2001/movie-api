package com.movie.exception;

public class MovieNotFoundException extends  RuntimeException{
    public MovieNotFoundException(String message){
        super(message);
    }
}

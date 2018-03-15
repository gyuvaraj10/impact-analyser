package com.impact.analyser.exceptions;

/**
 * Created by Yuvaraj on 14/03/2018.
 */
public class PageClassNotFoundException extends RuntimeException {

    public PageClassNotFoundException(Exception ex) {
        super(ex);
    }
}

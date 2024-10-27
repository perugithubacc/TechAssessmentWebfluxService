package com.assessment.techassessmentwebfluxservice;

import com.assessment.techassessmentwebfluxservice.exceptions.GlobalException;
import org.springframework.http.HttpStatus;

public class Exceptions {

    public static class CustomerNotFoundException extends GlobalException {
        public CustomerNotFoundException(String message) {
            super(message, HttpStatus.NOT_FOUND);
        }
    }

    public static class NoChangeFoundException extends GlobalException {
        public NoChangeFoundException(String message) {
            super(message, HttpStatus.NOT_FOUND);
        }
    }

    public static class ProductNotFoundException extends GlobalException {
        public ProductNotFoundException(String message) {
            super(message, HttpStatus.NOT_FOUND);
        }
    }
}

package com.assessment.techassessmentwebfluxservice.exceptions;

public record ErrorDetails (
     String message,
     String details,
     int errorCode
){}



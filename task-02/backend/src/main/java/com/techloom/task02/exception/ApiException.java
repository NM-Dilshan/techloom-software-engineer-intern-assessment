package com.techloom.task02.exception;
import org.springframework.http.HttpStatus;
public class ApiException extends RuntimeException { private final HttpStatus status; public ApiException(String message, HttpStatus status) { super(message); this.status = status; } public HttpStatus getStatus() { return status; } }

package com.techloom.task02.exception;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
@RestControllerAdvice
public class GlobalExceptionHandler { @ExceptionHandler(ApiException.class) ResponseEntity<Map<String,String>> api(ApiException e) { return ResponseEntity.status(e.getStatus()).body(Map.of("error", e.getMessage())); } @ExceptionHandler(Exception.class) ResponseEntity<Map<String,String>> generic(Exception e) { return ResponseEntity.internalServerError().body(Map.of("error", "Unexpected server error")); } }

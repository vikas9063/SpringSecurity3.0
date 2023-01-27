package com.vk.proj.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalException {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> errorHandle(Exception ex){
        Map<String,String> map = new HashMap();
        map.put("message",ex.getMessage());
        map.put("status","failed");
        map.put("statusCode", String.valueOf(HttpStatus.NOT_FOUND));
        return new ResponseEntity<>(map,HttpStatus.OK);
    }


}

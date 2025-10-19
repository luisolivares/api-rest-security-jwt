package com.olivares.api_rest_security_jwt.utils;

import com.olivares.api_rest_security_jwt.model.dto.response.ResponseApiDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class ResponseUtils {

    public ResponseEntity formatOKResponse(Object responseObject) {
        return new ResponseEntity(responseApiDTO(responseObject), HttpStatus.OK);
    }

    public ResponseApiDTO<Object> responseApiDTO(Object responseObject) {
        return new ResponseApiDTO<>(responseObject, null);
    }

}

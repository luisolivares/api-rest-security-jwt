package com.olivares.api_rest_security_jwt.controller;

import com.olivares.api_rest_security_jwt.utils.ResponseUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@RequestMapping(value = "/api/v1/")
@RequiredArgsConstructor
@Tag(name = "Salud de la API", description = "Endpoints para verificar el estado de la aplicación.")
public class HealthzController {

    @Value("${spring.application.name}")
    private String nombre;

    @Value("${application-description}")
    private String descripcion;

    @Value("${application-version}")
    private String version;

    private final ResponseUtils responseUtils;

    @Operation(summary = "Salud o Healthz de API REST.", description = "Endpoint para verificar la salud o healthz de nuestra API REST.")
    @GetMapping(value = "healthz", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity healtz() {
        HashMap<String, String> response = new HashMap<>();
        response.put("API", nombre);
        response.put("Descripción", descripcion);
        response.put("Project version", version);
        response.put("Java version", System.getProperty("java.version"));
        return responseUtils.formatOKResponse(response);
    }


}

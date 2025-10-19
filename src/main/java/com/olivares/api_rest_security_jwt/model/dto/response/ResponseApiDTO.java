package com.olivares.api_rest_security_jwt.model.dto.response;

import com.olivares.api_rest_security_jwt.exception.ApiError;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Estructura genérica para las respuestas de la API.")
public record ResponseApiDTO<T>(

        @Schema(
                description = "Contenido de la respuesta, puede ser cualquier tipo de objeto dependiendo de la operación.",
                example = "{\"id\": 1, \"nombre\": \"Juan Pérez\"}"
        )
        T data,

        @Schema(
                description = "Información del error en caso de que la operación falle.",
                implementation = ApiError.class
        )
        ApiError error
) {
}
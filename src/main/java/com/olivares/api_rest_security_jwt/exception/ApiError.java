package com.olivares.api_rest_security_jwt.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpStatus;

import java.util.List;


@Schema(description = "Estructura de error utilizada para representar detalles de una respuesta fallida de la API.")
public record ApiError(

        @Schema(
                description = "Código de estado HTTP que describe el tipo de error.",
                example = "BAD_REQUEST"
        )
        HttpStatus status,

        @Schema(
                description = "Mensaje general que describe el error ocurrido.",
                example = "Error de validación en los campos enviados."
        )
        String message,

        @Schema(
                description = "Lista de errores específicos o mensajes detallados asociados al fallo.",
                example = "[\"El campo 'nombre' es obligatorio\", \"El campo 'edad' debe ser mayor a 0\"]"
        )
        List<String> errors
) {
}

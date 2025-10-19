package com.olivares.api_rest_security_jwt.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Objeto de solicitud que representa los datos del token de un usuario.")
public record TokenResponse(String token, String refreshToken) {

}

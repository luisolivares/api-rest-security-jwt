package com.olivares.api_rest_security_jwt.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Objeto del request que representa el login")
public class LoginRequest {

    @Schema(description = "Correo electrónico del usuario", example = "usuario@ejemplo.com", required = true)
    @NotBlank(message = "El campo email no debe estar vacío")
    @Email(message = "El formato del email es inválido")
    private String email;

    @Schema(description = "Contraseña del usuario", example = "MiC0ntraseñaSegura123!", required = true)
    @NotBlank(message = "El campo password no debe estar vacío")
    private String password;

}

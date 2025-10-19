package com.olivares.api_rest_security_jwt.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Objeto del request que representa el rol del usuario del sistema")
public class RolRequest {

    @Schema(description = "Nombre del rol asignado al usuario", example = "ADMINISTRADOR", required = true)
    @NotBlank(message = "El campo rol no debe estar vacío")
    private String rol;

    @Schema(description = "Descripción del rol", example = "Administrador con acceso total al sistema", required = true)
    @NotBlank(message = "El campo rolDescripcion no debe estar vacío")
    private String rolDescripcion;

}

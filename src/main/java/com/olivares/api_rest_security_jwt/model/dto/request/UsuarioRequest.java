package com.olivares.api_rest_security_jwt.model.dto.request;

import com.olivares.api_rest_security_jwt.model.enumerated.Genero;
import com.olivares.api_rest_security_jwt.model.enumerated.TipoDocumento;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Objeto del request que representa los datos personales de un usuario.")
public class UsuarioRequest extends LoginRequest {

    @Schema(description = "Nombres del usuario", example = "Juan Carlos", required = true)
    @NotBlank(message = "El campo nombres no debe estar vacio")
    @NotNull(message = "El campo nombres no debe ser nulo")
    private String nombres;

    @Schema(description = "Apellidos del usuario", example = "Ramírez Torres", required = true)
    @NotBlank(message = "El campo apellidos no debe estar vacio")
    @NotNull(message = "El campo apellidos no debe ser nulo")
    private String apellidos;

    @Schema(description = "Genero del usuario", example = "MASCULINO", required = true)
    @NotNull(message = "El campo genero no debe ser nulo")
    @Enumerated(EnumType.STRING)
    private Genero genero;

    @Schema(description = "Tipo de documento del usuario", example = "CEDULA", required = true)
    @NotNull(message = "El campo tipoDocumento no debe ser nulo")
    @Enumerated(EnumType.STRING)
    private TipoDocumento tipoDocumento;

    @Schema(description = "Número de documento del usuario", example = "12345678", required = true)
    @NotBlank(message = "El campo numeroDocumento no debe estar vacio")
    @NotNull(message = "El campo numeroDocumento no debe ser nulo")
    private String numeroDocumento;

    @Schema(description = "Teléfono o correo electrónico del usuario", example = "987654321", required = true)
    @NotBlank(message = "El campo email no debe estar vacio")
    @NotNull(message = "El campo email no debe ser nulo")
    private String telefono;

    @Schema(description = "Rol asignado al usuario", example = "ADMINISTRADOR", required = true)
    @NotBlank(message = "El campo tipoRol no debe estar vacio")
    @NotNull(message = "El campo tipoRol no debe estar vacio")
    private String tipoRol;

}
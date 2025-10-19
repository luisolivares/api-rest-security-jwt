package com.olivares.api_rest_security_jwt.controller;

import com.olivares.api_rest_security_jwt.model.dto.request.LoginRequest;
import com.olivares.api_rest_security_jwt.model.dto.request.UsuarioRequest;
import com.olivares.api_rest_security_jwt.model.dto.response.ResponseApiDTO;
import com.olivares.api_rest_security_jwt.model.dto.response.TokenResponse;
import com.olivares.api_rest_security_jwt.model.dto.response.UsuarioDTO;
import com.olivares.api_rest_security_jwt.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Endpoints para registro y autenticación de usuarios.")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/registros")
    @Operation(
            summary = "Registrar un nuevo usuario",
            description = "Registra un nuevo usuario en el sistema. Los datos del usuario se envían en el cuerpo de la solicitud.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Usuario registrado exitosamente",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = UsuarioDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Datos de entrada inválidos",
                            content = @Content
                    )
            }
    )
    public ResponseEntity<ResponseApiDTO<UsuarioDTO>> registro(@Valid @RequestBody UsuarioRequest request) {
        return authService.registros(request);
    }


    @PostMapping("/token")
    @Operation(
            summary = "Obtener token de autenticación",
            description = "Autentica un usuario con sus credenciales y devuelve un token JWT para acceder a los recursos protegidos.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Token generado exitosamente",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = TokenResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Credenciales inválidas",
                            content = @Content
                    )
            }
    )
    public ResponseEntity<ResponseApiDTO<TokenResponse>> token(@RequestBody LoginRequest request) {
        return authService.token(request);
    }

}

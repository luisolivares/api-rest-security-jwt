package com.olivares.api_rest_security_jwt.controller;

import com.olivares.api_rest_security_jwt.annotations.RolAdministrador;
import com.olivares.api_rest_security_jwt.annotations.TieneRol;
import com.olivares.api_rest_security_jwt.model.dto.request.UsuarioRequest;
import com.olivares.api_rest_security_jwt.model.dto.response.ResponseApiDTO;
import com.olivares.api_rest_security_jwt.model.enumerated.TipoDocumento;
import com.olivares.api_rest_security_jwt.service.UsuarioServiceImpl;
import com.olivares.api_rest_security_jwt.utils.ResponseUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Log4j2
@RestController
@RequestMapping(value = "/api/v1/usuarios")
@RequiredArgsConstructor
@Tag(name = "Usuarios", description = "Endpoints para la gestión de usuarios del sistema.")
public class UsuarioController {

    private final UsuarioServiceImpl usuarioServiceImpl;

    private final ResponseUtils responseUtils;

    @RolAdministrador
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(
            summary = "Modificar un usuario existente (Solo para Administradores)",
            description = "Permite a un usuario con rol 'ADMINISTRADOR' modificar la información de un usuario. Se debe proporcionar un objeto 'UsuarioRequest' con los datos actualizados.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Usuario modificado exitosamente",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ResponseApiDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos",
                            content = @Content),
                    @ApiResponse(responseCode = "403", description = "Acceso denegado (Requiere rol ADMINISTRADOR)",
                            content = @Content),
                    @ApiResponse(responseCode = "404", description = "Usuario no encontrado",
                            content = @Content)
            }
    )
    @PutMapping(value = "/")
    public ResponseEntity<ResponseApiDTO<Object>> modificar(@Parameter(description = "Objeto usuario a crear", required = true) @Valid @RequestBody UsuarioRequest request) {
        log.info("Iniciando usuario crear que solo puede hacerlo los usuarios con ROL ADMINISTRADOR");
        return new ResponseEntity<>(responseUtils.responseApiDTO(usuarioServiceImpl.modificar(request)), HttpStatus.OK);
    }

    @TieneRol
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(
            summary = "Listar usuarios paginados (Todos los roles)",
            description = "Permite a los usuarios con cualquier rol (ADMINISTRADOR, ESTANDAR, INVITADO) consultar el listado de usuarios con paginación.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Listado de usuarios obtenido exitosamente",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ResponseApiDTO.class))),
                    @ApiResponse(responseCode = "403", description = "Acceso denegado",
                            content = @Content)
            }
    )
    @GetMapping("/")
    public ResponseEntity<ResponseApiDTO<Object>> listado(
            @RequestParam(required = true, name = "pageNo", defaultValue = "0") @Min(0) int pageNo,
            @RequestParam(required = true, name = "pageSize", defaultValue = "10") @Min(1) @Max(100) int pageSize
    ) {
        log.info("Listado de usuarios donde puede acceder todos los roles del sistemas (ADMINISTRADOR, ESTANDAR, INVITADO)");
        return new ResponseEntity<>(responseUtils.responseApiDTO(usuarioServiceImpl.listadoPorPaginacion(pageNo, pageSize)), HttpStatus.OK);
    }

    @RolAdministrador
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(
            summary = "Eliminar un usuario (Solo para Administradores)",
            description = "Permite a un usuario con rol 'ADMINISTRADOR' eliminar a un usuario por su tipo y número de documento.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Usuario eliminado exitosamente"),
                    @ApiResponse(responseCode = "403", description = "Acceso denegado (Requiere rol ADMINISTRADOR)",
                            content = @Content),
                    @ApiResponse(responseCode = "404", description = "Usuario no encontrado",
                            content = @Content)
            }
    )
    @DeleteMapping(value = "/{tipoDocumento}/{documento}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> delete(
            @Parameter(description = "Tipo de documento de la persona", required = true) @PathVariable("tipoDocumento") TipoDocumento tipoDocumento,
            @Parameter(description = "Número de documento de la persona", required = true) @PathVariable("documento") String documento
    ) {
        log.info("Iniciando usuario crear que solo puede hacerlo los usuarios con ROL ADMINISTRADOR");
        usuarioServiceImpl.eliminar(tipoDocumento, documento);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
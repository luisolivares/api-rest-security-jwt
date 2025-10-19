package com.olivares.api_rest_security_jwt.controller;

import com.olivares.api_rest_security_jwt.annotations.RolAdministrador;
import com.olivares.api_rest_security_jwt.annotations.TieneRol;
import com.olivares.api_rest_security_jwt.model.dto.request.RolRequest;
import com.olivares.api_rest_security_jwt.model.dto.response.ResponseApiDTO;
import com.olivares.api_rest_security_jwt.service.RolServiceImpl;
import com.olivares.api_rest_security_jwt.utils.ResponseUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
@RequestMapping(value = "/api/v1/roles")
@RequiredArgsConstructor
@Tag(name = "Roles", description = "Endpoints para la gestión de roles del sistema.")
public class RolController {

    private final RolServiceImpl rolServiceImpl;
    private final ResponseUtils responseUtils;

    @RolAdministrador
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping(value = "/")
    @Operation(summary = "Crear un nuevo rol", description = "Endpoint para crear un nuevo rol. Requiere permisos de Administrador.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Rol creado exitosamente",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ResponseApiDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada no válidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "No autorizado para crear roles")
    })
    public ResponseEntity<Object> crear(
            @Valid @RequestBody RolRequest request
    ) {
        log.info("Iniciando creación de rol que solo puede hacerlo los usuarios con ROL ADMINISTRADOR");
        return new ResponseEntity<>(responseUtils.responseApiDTO(rolServiceImpl.crear(request)), HttpStatus.CREATED);
    }

    @RolAdministrador
    @SecurityRequirement(name = "Bearer Authentication")
    @PutMapping(value = "/")
    @Operation(summary = "Modificar un rol existente", description = "Endpoint para modificar un rol. Requiere permisos de Administrador.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rol modificado exitosamente",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ResponseApiDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada no válidos o rol no encontrado"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "No autorizado para modificar roles")
    })
    public ResponseEntity<ResponseApiDTO<Object>> modificar(
            @Parameter(description = "Objeto con los datos del rol a modificar", required = true) @Valid @RequestBody RolRequest request) {
        log.info("Iniciando modificación del rol que solo puede hacerlo los usuarios con ROL ADMINISTRADOR");
        return new ResponseEntity<>(responseUtils.responseApiDTO(rolServiceImpl.modificar(request)), HttpStatus.OK);
    }

    @TieneRol
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/")
    @Operation(summary = "Obtener listado de roles", description = "Endpoint para listar los roles existentes con paginación. Accesible para todos los roles del sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listado de roles obtenido exitosamente",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ResponseApiDTO.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    public ResponseEntity<ResponseApiDTO<Object>> listado(
            @Parameter(description = "Número de página (mínimo 0)", required = false)
            @RequestParam(required = false, name = "pageNo", defaultValue = "0") @Min(0) int pageNo,
            @Parameter(description = "Tamaño de página (mínimo 1, máximo 100)", required = false)
            @RequestParam(required = false, name = "pageSize", defaultValue = "10") @Min(1) @Max(100) int pageSize
    ) {
        log.info("Listado de roles donde puede acceder todos los usuarios con los roles del sistema.");
        return new ResponseEntity<>(responseUtils.responseApiDTO(rolServiceImpl.listadoPorPaginacion(pageNo, pageSize)), HttpStatus.OK);
    }

    @RolAdministrador
    @SecurityRequirement(name = "Bearer Authentication")
    @DeleteMapping(value = "/{tipoRol}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Eliminar un rol", description = "Endpoint para eliminar un rol por su tipo. Requiere permisos de Administrador.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Rol eliminado exitosamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "No autorizado para eliminar roles"),
            @ApiResponse(responseCode = "404", description = "Rol no encontrado")
    })
    public ResponseEntity<Object> delete(
            @Parameter(description = "Tipo de rol del sistema a eliminar", required = true) @PathVariable("tipoRol") String tipoRol
    ) {
        log.info("Iniciando eliminación de rol que solo puede hacerlo los usuarios con ROL ADMINISTRADOR");
        rolServiceImpl.eliminar(tipoRol);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
package com.olivares.api_rest_security_jwt.model.dto.response;

import java.util.List;

public record UsuarioDTO(Long id,
                         String nombres,
                         String apellidos,
                         String genero,
                         String tipoDocumento,
                         String numeroDocumento,
                         String email,
                         String telefono,
                         List<RolDTO> roles) {
}

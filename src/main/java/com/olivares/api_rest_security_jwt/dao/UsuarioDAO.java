package com.olivares.api_rest_security_jwt.dao;

import com.olivares.api_rest_security_jwt.model.dto.request.UsuarioRequest;
import com.olivares.api_rest_security_jwt.model.dto.response.UsuarioDTO;
import com.olivares.api_rest_security_jwt.model.enumerated.TipoDocumento;

import java.util.List;
import java.util.Optional;

public interface UsuarioDAO {

    List<UsuarioDTO> listadoPorPaginacion(int pageNo, int pageSize);

    Optional<UsuarioDTO> crear(UsuarioRequest request);

    Optional<UsuarioDTO> modificar(UsuarioRequest request);

    Optional<UsuarioDTO> buscarPorTipoYNumeroDocumento(TipoDocumento tipoDocumento, String documento);

    void eliminar(TipoDocumento tipoDocumento, String documento);

}

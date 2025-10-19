package com.olivares.api_rest_security_jwt.dao;

import com.olivares.api_rest_security_jwt.model.dto.request.RolRequest;
import com.olivares.api_rest_security_jwt.model.dto.response.RolDTO;

import java.util.List;
import java.util.Optional;

public interface RolDAO {

    List<RolDTO> listadoPorPaginacion(Integer pageNo, Integer pageSize);

    Optional<RolDTO> crear(RolRequest request);

    Optional<RolDTO> modificar(RolRequest request);

    Optional<RolDTO> buscarPorTipoRol(String tipoRol);

    void eliminar(String tipoRol);

}

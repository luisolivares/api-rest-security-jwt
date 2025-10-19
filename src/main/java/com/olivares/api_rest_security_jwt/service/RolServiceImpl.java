package com.olivares.api_rest_security_jwt.service;

import com.olivares.api_rest_security_jwt.dao.RolDAO;
import com.olivares.api_rest_security_jwt.exception.RolNotFoundException;
import com.olivares.api_rest_security_jwt.model.dto.request.RolRequest;
import com.olivares.api_rest_security_jwt.model.dto.response.RolDTO;
import com.olivares.api_rest_security_jwt.model.entity.Rol;
import com.olivares.api_rest_security_jwt.repository.RolRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor
public class RolServiceImpl implements RolDAO {

    private final RolRepository rolRepository;

    private String errorException;

    @Override
    @Transactional(readOnly = true)
    public List<RolDTO> listadoPorPaginacion(Integer pageNo, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Rol> response = rolRepository.findAll(pageable);

        List<RolDTO> roles = new ArrayList<>();

        if (response.hasContent()) {
            roles = response.getContent()
                    .stream()
                    .map(rol ->
                            new RolDTO(rol.getId(), rol.getTipoRol(), rol.getDescripcion()))
                    .toList();
        }

        return roles;
    }

    @Override
    @Transactional
    public Optional<RolDTO> crear(RolRequest request) {

        Optional<Rol> rolBusqueda = rolRepository.buscarTipoRol(request.getRol());

        if (rolBusqueda.isPresent()) {
            errorException = String.format("Rol existente en sistema %s", request.getRol());
            throw new RolNotFoundException(errorException);
        }

        Rol rolEntity = new Rol();
        rolEntity.setTipoRol(request.getRol().toUpperCase());
        rolEntity.setDescripcion(request.getRolDescripcion());

        Optional<Rol> rol = Optional.of(rolRepository.saveAndFlush(rolEntity));

        RolDTO rolDTO = new RolDTO(rol.get().getId(), rol.get().getTipoRol(), rol.get().getDescripcion());

        return Optional.of(rolDTO);
    }

    @Override
    @Transactional
    public Optional<RolDTO> modificar(RolRequest request) {

        Optional<Rol> rolBusqueda = rolRepository.buscarTipoRol(request.getRol());

        if (rolBusqueda.isEmpty()) {
            errorException = String.format("Rol inexistente en sistema %s", request.getRol());
            throw new RolNotFoundException(errorException);
        }

        Rol rolEntity = rolBusqueda.get();

        rolEntity.setId(rolEntity.getId());
        rolEntity.setTipoRol(request.getRol().toUpperCase());
        rolEntity.setDescripcion(request.getRolDescripcion());

        Optional<Rol> rol = Optional.of(rolRepository.saveAndFlush(rolEntity));

        RolDTO rolDTO = new RolDTO(rol.get().getId(), rol.get().getTipoRol(), rol.get().getDescripcion());

        return Optional.of(rolDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RolDTO> buscarPorTipoRol(String tipoRol) {
        Optional<Rol> rol = rolRepository.buscarTipoRol(tipoRol);
        RolDTO rolDTO = new RolDTO(rol.get().getId(), rol.get().getTipoRol(), rol.get().getDescripcion());
        return Optional.ofNullable(rolDTO);
    }

    @Override
    @Transactional
    public void eliminar(String tipoRol) {
        Optional<Rol> rol = rolRepository.buscarTipoRol(tipoRol);

        if (rol.isEmpty()) {
            errorException = String.format("Rol inexistente en sistema %s", tipoRol);
            throw new RolNotFoundException(errorException);
        }

        rolRepository.delete(rol.get());
    }
}

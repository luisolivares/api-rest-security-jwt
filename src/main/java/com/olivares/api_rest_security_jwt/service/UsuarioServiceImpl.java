package com.olivares.api_rest_security_jwt.service;

import com.olivares.api_rest_security_jwt.dao.UsuarioDAO;
import com.olivares.api_rest_security_jwt.exception.UsuarioNotFoundException;
import com.olivares.api_rest_security_jwt.model.dto.request.UsuarioRequest;
import com.olivares.api_rest_security_jwt.model.dto.response.RolDTO;
import com.olivares.api_rest_security_jwt.model.dto.response.UsuarioDTO;
import com.olivares.api_rest_security_jwt.model.entity.Usuario;
import com.olivares.api_rest_security_jwt.model.enumerated.TipoDocumento;
import com.olivares.api_rest_security_jwt.repository.UsuarioRepository;
import com.olivares.api_rest_security_jwt.utils.ObjectMapperUtils;
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
public class UsuarioServiceImpl implements UsuarioDAO {

    private final UsuarioRepository usuarioRepository;

    private String errorException;

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioDTO> listadoPorPaginacion(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Usuario> response = usuarioRepository.findAll(pageable);

        List<UsuarioDTO> usuariosDTO = new ArrayList<>();

        if (response.hasContent()) {

            usuariosDTO = response.stream()
                    .map(usuario -> new UsuarioDTO(
                            usuario.getId(),
                            usuario.getNombres(),
                            usuario.getApellidos(),
                            usuario.getGenero().name(),
                            usuario.getTipoDocumento().name(),
                            usuario.getNumeroDocumento(),
                            usuario.getEmail(),
                            usuario.getTelefono(),
                            usuario.getRoles().stream()
                                    .map(rol -> new RolDTO(rol.getId(), rol.getTipoRol(), rol.getDescripcion()))
                                    .toList()
                    ))
                    .toList();

        }

        return usuariosDTO;
    }

    @Override
    @Transactional
    public Optional<UsuarioDTO> crear(UsuarioRequest request) {

        Optional<Usuario> usuarioBusqueda = usuarioRepository.buscarPorEmail(request.getEmail());

        if (usuarioBusqueda.isPresent()) {
            errorException = String.format("Usuario existente en sistema con el email %s", request.getEmail());
            throw new UsuarioNotFoundException(errorException);
        }

        Usuario usuarioEntity = ObjectMapperUtils.map(request, Usuario.class);

        Optional<Usuario> usuario = Optional.of(usuarioRepository.saveAndFlush(usuarioEntity));

        UsuarioDTO usuarioDTO = ObjectMapperUtils.map(usuario.get(), UsuarioDTO.class);

        return Optional.ofNullable(usuarioDTO);
    }

    @Override
    @Transactional
    public Optional<UsuarioDTO> modificar(UsuarioRequest request) {

        Optional<Usuario> usuarioBusqueda = usuarioRepository.buscarPorEmail(request.getEmail());


        if (usuarioBusqueda.isEmpty()) {
            errorException = String.format("Usuario no existente en sistema con el email %s", request.getEmail());
            throw new UsuarioNotFoundException(errorException);
        }

        Usuario usuarioEntity = ObjectMapperUtils.map(request, Usuario.class);

        usuarioEntity.setId(usuarioBusqueda.get().getId());
        usuarioEntity.setRoles(usuarioBusqueda.get().getRoles());

        Optional<Usuario> usuario = Optional.of(usuarioRepository.saveAndFlush(usuarioEntity));

        List<RolDTO> rolesDTO = usuario.get().getRoles().stream()
                .map((element) -> new RolDTO(element.getId(), element.getTipoRol(), element.getDescripcion()))
                .toList();

        UsuarioDTO usuarioDTO = new UsuarioDTO(
                usuario.get().getId(),
                usuario.get().getNombres(),
                usuario.get().getApellidos(),
                usuario.get().getGenero().name(),
                usuario.get().getTipoDocumento().name(),
                usuario.get().getNumeroDocumento(),
                usuario.get().getEmail(),
                usuario.get().getTelefono(),
                rolesDTO);

        return Optional.of(usuarioDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UsuarioDTO> buscarPorTipoYNumeroDocumento(TipoDocumento tipoDocumento, String documento) {
        Optional<Usuario> usuario = usuarioRepository.buscarPorDocumento(tipoDocumento, documento);
        UsuarioDTO usuarioDTO = ObjectMapperUtils.map(usuario.get(), UsuarioDTO.class);
        return Optional.ofNullable(usuarioDTO);
    }

    @Override
    @Transactional
    public void eliminar(TipoDocumento tipoDocumento, String documento) {

        Optional<Usuario> usuario = usuarioRepository.buscarPorDocumento(tipoDocumento, documento);

        if (usuario.isEmpty()) {
            errorException = String.format("Usuario cuyo documento es %s %s no existe en sistema", tipoDocumento, documento);
            throw new UsuarioNotFoundException(errorException);
        }

        usuarioRepository.eliminarPorTipoYNumeroDocumento(tipoDocumento, documento);
    }

}
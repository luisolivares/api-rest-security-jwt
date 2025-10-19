package com.olivares.api_rest_security_jwt.service;

import com.olivares.api_rest_security_jwt.exception.PasswordException;
import com.olivares.api_rest_security_jwt.exception.UsuarioNotFoundException;
import com.olivares.api_rest_security_jwt.model.dto.request.LoginRequest;
import com.olivares.api_rest_security_jwt.model.dto.request.UsuarioRequest;
import com.olivares.api_rest_security_jwt.model.dto.response.ResponseApiDTO;
import com.olivares.api_rest_security_jwt.model.dto.response.RolDTO;
import com.olivares.api_rest_security_jwt.model.dto.response.TokenResponse;
import com.olivares.api_rest_security_jwt.model.dto.response.UsuarioDTO;
import com.olivares.api_rest_security_jwt.model.entity.Rol;
import com.olivares.api_rest_security_jwt.model.entity.Usuario;
import com.olivares.api_rest_security_jwt.repository.RolRepository;
import com.olivares.api_rest_security_jwt.repository.UsuarioRepository;
import com.olivares.api_rest_security_jwt.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    private String errorAuthService;

    @Transactional(readOnly = true)
    public ResponseEntity<ResponseApiDTO<TokenResponse>> token(LoginRequest request) {

        errorAuthService = String.format("El usuario cuyo correo electronico %s no existe en el sistema", request.getEmail());

        var usuarioDetalles = usuarioRepository.buscarPorEmail(request.getEmail())
                .orElseThrow(() -> new UsuarioNotFoundException(errorAuthService)
                );

        if (!passwordEncoder.matches(request.getPassword(), usuarioDetalles.getPassword())) {
            throw new PasswordException("Contraseña incorrecta");
        }

        UserDetails userDetails = User
                .withUsername(usuarioDetalles.getEmail())
                .password(usuarioDetalles.getPassword())
                .authorities(usuarioDetalles.getRoles().stream()
                        .map(r -> "ROLE_" + r.getTipoRol())
                        .toArray(String[]::new))
                .build();

        var token = jwtService.generateToken(userDetails);

        var refreshToken = jwtService.generateRefreshToken(userDetails);

        TokenResponse tokenResponse = new TokenResponse(token, refreshToken);

        ResponseApiDTO<TokenResponse> response = new ResponseApiDTO<>(tokenResponse, null);

        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @Transactional
    public ResponseEntity<ResponseApiDTO<UsuarioDTO>> registros(UsuarioRequest req) {
        if (usuarioRepository.buscarPorEmail(req.getEmail()).isPresent()) {
            errorAuthService = String.format("Usuario con el correo electronico %s existente en el sistema.", req.getEmail());
            log.error(errorAuthService);
            throw new UsuarioNotFoundException(errorAuthService);
        }

        Optional<Rol> rol = rolRepository.buscarTipoRol(req.getTipoRol());
        if (rol.isEmpty()) {
            // Crear rol si no existe
            rol = Optional.ofNullable(Rol.builder().tipoRol(req.getTipoRol().toUpperCase()).build());
            Rol rolEntity = rol.get();
            rol = Optional.of(rolRepository.save(rolEntity));
        }

        Usuario usuario = Usuario.builder()
                .nombres(req.getNombres())
                .apellidos(req.getApellidos())
                .tipoDocumento(req.getTipoDocumento())
                .numeroDocumento(req.getNumeroDocumento())
                .genero(req.getGenero())
                .telefono(req.getTelefono())
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .roles(new HashSet<>(Collections.singletonList(rol.get())))
                .build();

        rol.get().setUsuario(usuario);

        Usuario response = usuarioRepository.saveAndFlush(usuario);

        List<RolDTO> rolesDTO = response.getRoles().stream()
                .map((element) -> new RolDTO(element.getId(), element.getTipoRol(), element.getDescripcion()))
                .toList();

        UsuarioDTO usuarioDTO = new UsuarioDTO(
                response.getId(),
                response.getNombres(),
                response.getApellidos(),
                response.getGenero().name(),
                response.getTipoDocumento().name(),
                response.getNumeroDocumento(),
                response.getEmail(),
                response.getTelefono(),
                rolesDTO);

        ResponseApiDTO<UsuarioDTO> responseApiDTO = new ResponseApiDTO<>(usuarioDTO, null);

        return new ResponseEntity<>(responseApiDTO, HttpStatus.CREATED);
    }


}
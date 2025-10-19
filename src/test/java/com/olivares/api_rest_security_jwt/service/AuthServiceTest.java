package com.olivares.api_rest_security_jwt.service;

import com.olivares.api_rest_security_jwt.exception.PasswordException;
import com.olivares.api_rest_security_jwt.exception.UsuarioNotFoundException;
import com.olivares.api_rest_security_jwt.model.dto.request.LoginRequest;
import com.olivares.api_rest_security_jwt.model.dto.request.UsuarioRequest;
import com.olivares.api_rest_security_jwt.model.entity.Rol;
import com.olivares.api_rest_security_jwt.model.entity.Usuario;
import com.olivares.api_rest_security_jwt.model.enumerated.Genero;
import com.olivares.api_rest_security_jwt.model.enumerated.TipoDocumento;
import com.olivares.api_rest_security_jwt.repository.RolRepository;
import com.olivares.api_rest_security_jwt.repository.UsuarioRepository;
import com.olivares.api_rest_security_jwt.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private RolRepository rolRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testTokenExito() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("user@example.com");
        loginRequest.setPassword("password123");

        Usuario usuario = new Usuario();
        usuario.setEmail("user@example.com");
        usuario.setPassword("encodedPassword");
        Rol rol = new Rol();
        rol.setTipoRol("USER");
        Set<Rol> roles = new HashSet<>();
        roles.add(rol);
        usuario.setRoles(roles);

        when(usuarioRepository.buscarPorEmail(loginRequest.getEmail())).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches(loginRequest.getPassword(), usuario.getPassword())).thenReturn(true);
        when(jwtService.generateToken(any(UserDetails.class))).thenReturn("token");
        when(jwtService.generateRefreshToken(any(UserDetails.class))).thenReturn("refreshToken");

        ResponseEntity<?> response = authService.token(loginRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(usuarioRepository, times(1)).buscarPorEmail(loginRequest.getEmail());
        verify(passwordEncoder, times(1)).matches(loginRequest.getPassword(), usuario.getPassword());
        verify(jwtService, times(1)).generateToken(any(UserDetails.class));
        verify(jwtService, times(1)).generateRefreshToken(any(UserDetails.class));
    }

    @Test
    void testTokenUsuarioNoEncontrado() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("nonexistent@example.com");
        loginRequest.setPassword("password123");

        when(usuarioRepository.buscarPorEmail(loginRequest.getEmail())).thenReturn(Optional.empty());

        UsuarioNotFoundException exception = assertThrows(UsuarioNotFoundException.class, () -> {
            authService.token(loginRequest);
        });

        assertTrue(exception.getMessage().contains("no existe en el sistema"));
        verify(usuarioRepository, times(1)).buscarPorEmail(loginRequest.getEmail());
        verifyNoMoreInteractions(passwordEncoder, jwtService);
    }

    @Test
    void testTokenPasswordIncorrecta() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("user@example.com");
        loginRequest.setPassword("wrongPassword");

        Usuario usuario = new Usuario();
        usuario.setEmail("user@example.com");
        usuario.setPassword("encodedPassword");

        when(usuarioRepository.buscarPorEmail(loginRequest.getEmail())).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches(loginRequest.getPassword(), usuario.getPassword())).thenReturn(false);

        PasswordException exception = assertThrows(PasswordException.class, () -> {
            authService.token(loginRequest);
        });

        assertEquals("Contraseña incorrecta", exception.getMessage());
        verify(usuarioRepository, times(1)).buscarPorEmail(loginRequest.getEmail());
        verify(passwordEncoder, times(1)).matches(loginRequest.getPassword(), usuario.getPassword());
        verifyNoMoreInteractions(jwtService);
    }

    @Test
    void testRegistrosCrearUsuarioConRolExistente() {
        UsuarioRequest request = new UsuarioRequest();
        request.setEmail("newuser@example.com");
        request.setPassword("password123");
        request.setNombres("Nombre");
        request.setApellidos("Apellido");
        request.setGenero(Genero.MASCULINO); // o el valor correspondiente
        request.setTipoDocumento(TipoDocumento.CEDULA); // o el valor correspondiente
        request.setNumeroDocumento("123456");
        request.setTelefono("1234567890");
        request.setTipoRol("USER");

        Rol rolExistente = new Rol();
        rolExistente.setTipoRol("USER");
        rolExistente.setDescripcion("Usuario normal");

        when(usuarioRepository.buscarPorEmail(request.getEmail())).thenReturn(Optional.empty());
        when(rolRepository.buscarTipoRol("USER")).thenReturn(Optional.of(rolExistente));
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");

        Usuario usuarioGuardado = new Usuario();
        usuarioGuardado.setId(1L);
        usuarioGuardado.setEmail(request.getEmail());
        usuarioGuardado.setPassword("encodedPassword");
        usuarioGuardado.setRoles(Set.of(rolExistente));
        usuarioGuardado.setNombres(request.getNombres());
        usuarioGuardado.setApellidos(request.getApellidos());
        usuarioGuardado.setGenero(request.getGenero());
        usuarioGuardado.setTipoDocumento(request.getTipoDocumento());
        usuarioGuardado.setNumeroDocumento(request.getNumeroDocumento());
        usuarioGuardado.setTelefono(request.getTelefono());

        when(usuarioRepository.saveAndFlush(any(Usuario.class))).thenReturn(usuarioGuardado);

        ResponseEntity<?> response = authService.registros(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(usuarioRepository, times(1)).buscarPorEmail(request.getEmail());
        verify(rolRepository, times(1)).buscarTipoRol("USER");
        verify(passwordEncoder, times(1)).encode(request.getPassword());
        verify(usuarioRepository, times(1)).saveAndFlush(any(Usuario.class));
    }

    @Test
    void testRegistrosCrearUsuarioConRolNuevo() {
        UsuarioRequest request = new UsuarioRequest();
        request.setEmail("newuser2@example.com");
        request.setPassword("password123");
        request.setNombres("Nombre2");
        request.setApellidos("Apellido2");
        request.setGenero(Genero.MASCULINO);
        request.setTipoDocumento(TipoDocumento.CEDULA);
        request.setNumeroDocumento("654321");
        request.setTelefono("0987654321");
        request.setTipoRol("ADMINISTRADOR");

        when(usuarioRepository.buscarPorEmail(request.getEmail())).thenReturn(Optional.empty());
        when(rolRepository.buscarTipoRol("ADMINISTRADOR")).thenReturn(Optional.empty());

        Rol nuevoRol = new Rol();
        nuevoRol.setTipoRol("ADMINISTRADOR");
        nuevoRol.setDescripcion("Administrador");

        when(rolRepository.save(any(Rol.class))).thenReturn(nuevoRol);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");

        Usuario usuarioGuardado = new Usuario();
        usuarioGuardado.setId(2L);
        usuarioGuardado.setEmail(request.getEmail());
        usuarioGuardado.setPassword("encodedPassword");
        usuarioGuardado.setRoles(Set.of(nuevoRol));
        usuarioGuardado.setNombres(request.getNombres());
        usuarioGuardado.setApellidos(request.getApellidos());
        usuarioGuardado.setGenero(request.getGenero());
        usuarioGuardado.setTipoDocumento(request.getTipoDocumento());
        usuarioGuardado.setNumeroDocumento(request.getNumeroDocumento());
        usuarioGuardado.setTelefono(request.getTelefono());

        when(usuarioRepository.saveAndFlush(any(Usuario.class))).thenReturn(usuarioGuardado);

        ResponseEntity<?> response = authService.registros(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(usuarioRepository, times(1)).buscarPorEmail(request.getEmail());
        verify(rolRepository, times(1)).buscarTipoRol("ADMINISTRADOR");
        verify(rolRepository, times(1)).save(any(Rol.class));
        verify(passwordEncoder, times(1)).encode(request.getPassword());
        verify(usuarioRepository, times(1)).saveAndFlush(any(Usuario.class));
    }

    @Test
    void testRegistrosUsuarioExistente() {
        UsuarioRequest request = new UsuarioRequest();
        request.setEmail("existing@example.com");

        Usuario usuarioExistente = new Usuario();
        usuarioExistente.setEmail("existing@example.com");

        when(usuarioRepository.buscarPorEmail(request.getEmail())).thenReturn(Optional.of(usuarioExistente));

        UsuarioNotFoundException exception = assertThrows(UsuarioNotFoundException.class, () -> {
            authService.registros(request);
        });

        assertTrue(exception.getMessage().contains("existente en el sistema"));
        verify(usuarioRepository, times(1)).buscarPorEmail(request.getEmail());
        verifyNoMoreInteractions(rolRepository, passwordEncoder, usuarioRepository);
    }
}

package com.olivares.api_rest_security_jwt.service;

import com.olivares.api_rest_security_jwt.model.entity.Rol;
import com.olivares.api_rest_security_jwt.model.entity.Usuario;
import com.olivares.api_rest_security_jwt.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UsuarioDetallesServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    private UsuarioDetallesService usuarioDetallesService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        usuarioDetallesService = new UsuarioDetallesService(usuarioRepository);
    }

    @Test
    void testLoadUserByUsername_usuarioEncontrado() {
        String email = "test@example.com";

        Rol rol = new Rol();
        rol.setTipoRol("ADMIN");

        Set<Rol> roles = new HashSet<>();
        roles.add(rol);

        Usuario usuario = new Usuario();
        usuario.setEmail(email);
        usuario.setPassword("encodedPassword");
        usuario.setRoles(roles);

        when(usuarioRepository.buscarPorEmail(email)).thenReturn(Optional.of(usuario));

        UserDetails userDetails = usuarioDetallesService.loadUserByUsername(email);

        assertNotNull(userDetails);
        assertEquals(email, userDetails.getUsername());
        assertEquals("encodedPassword", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")));

        verify(usuarioRepository, times(1)).buscarPorEmail(email);
    }

    @Test
    void testLoadUserByUsername_usuarioNoEncontrado() {
        String email = "noexist@example.com";

        when(usuarioRepository.buscarPorEmail(email)).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
            usuarioDetallesService.loadUserByUsername(email);
        });

        assertEquals("Usuario no encontrado", exception.getMessage());

        verify(usuarioRepository, times(1)).buscarPorEmail(email);
    }
}

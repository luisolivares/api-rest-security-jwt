package com.olivares.api_rest_security_jwt.service;

import com.olivares.api_rest_security_jwt.model.entity.Rol;
import com.olivares.api_rest_security_jwt.repository.RolRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class CustomSecurityServiceTest {

    @Mock
    private RolRepository rolRepository;

    @InjectMocks
    private CustomSecurityService customSecurityService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.clearContext(); // Limpiar el contexto antes de cada prueba
    }

    @Test
    void testUsuarioTieneRolConRolValido() {
        // Simular roles que hay en BD
        Rol rol1 = new Rol();
        rol1.setTipoRol("ADMINISTRADOR");
        Rol rol2 = new Rol();
        rol2.setTipoRol("USER");

        when(rolRepository.findAll()).thenReturn(List.of(rol1, rol2));

        // Simular usuario autenticado con rol USER
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                "usuario",
                "password",
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        boolean resultado = customSecurityService.usuarioTieneRol();

        assertTrue(resultado, "El usuario debería tener un rol válido");
        verify(rolRepository, times(1)).findAll();
    }

    @Test
    void testUsuarioTieneRolSinRolValido() {
        // Simular roles en BD solo ADMIN
        Rol rol1 = new Rol();
        rol1.setTipoRol("ADMINISTRADOR");

        when(rolRepository.findAll()).thenReturn(List.of(rol1));

        // Usuario autenticado con rol que no está en BD
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                "usuario",
                "password",
                List.of(new SimpleGrantedAuthority("ROLE_GUEST"))
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        boolean resultado = customSecurityService.usuarioTieneRol();

        assertFalse(resultado, "El usuario no tiene ningún rol válido");
        verify(rolRepository, times(1)).findAll();
    }

    @Test
    void testUsuarioTieneRol_sinAutenticacion() {
        // No hay usuario autenticado
        SecurityContextHolder.getContext().setAuthentication(null);

        boolean resultado = customSecurityService.usuarioTieneRol();

        assertFalse(resultado, "No hay usuario autenticado, debería retornar false");
        verifyNoInteractions(rolRepository);
    }
}
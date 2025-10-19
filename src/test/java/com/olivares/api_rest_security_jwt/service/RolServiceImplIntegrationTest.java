package com.olivares.api_rest_security_jwt.service;

import com.olivares.api_rest_security_jwt.exception.RolNotFoundException;
import com.olivares.api_rest_security_jwt.model.dto.request.RolRequest;
import com.olivares.api_rest_security_jwt.model.dto.response.RolDTO;
import com.olivares.api_rest_security_jwt.model.entity.Rol;
import com.olivares.api_rest_security_jwt.repository.RolRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class RolServiceImplIntegrationTest {

    @Autowired
    private RolServiceImpl rolService;

    @Autowired
    private RolRepository rolRepository;

    private Rol rolExistente;

    @BeforeEach
    void setUp() {
        rolRepository.deleteAll();

        rolExistente = new Rol();
        rolExistente.setTipoRol("ADMINISTRADOR");
        rolExistente.setDescripcion("Administrador del sistema");

        rolRepository.saveAndFlush(rolExistente);
    }

    @Test
    @Order(1)
    @DisplayName("Listado por paginación de roles")
    void testListadoPorPaginacion() {
        List<RolDTO> roles = rolService.listadoPorPaginacion(0, 10);

        assertNotNull(roles);
        assertFalse(roles.isEmpty());
        assertEquals("ADMINISTRADOR", roles.get(0).rol());
    }

    @Test
    @Order(2)
    @DisplayName("Crear rol exitosamente")
    void testCrearRol() {
        RolRequest request = new RolRequest();
        request.setRol("USER");
        request.setRolDescripcion("Usuario estándar");

        Optional<RolDTO> rolCreado = rolService.crear(request);

        assertTrue(rolCreado.isPresent());
        assertEquals("USER", rolCreado.get().rol());
        assertEquals("Usuario estándar", rolCreado.get().rolDescripcion());

        // Verificar en BD
        Optional<Rol> enBD = rolRepository.buscarTipoRol("USER");
        assertTrue(enBD.isPresent());
        assertEquals("USER", enBD.get().getTipoRol());
    }

    @Test
    @DisplayName("Crear rol existente lanza excepción")
    void testCrearRolExistente() {
        RolRequest request = new RolRequest();
        request.setRol("ADMINISTRADOR");
        request.setRolDescripcion("Administrador duplicado");

        RolNotFoundException ex = assertThrows(RolNotFoundException.class, () -> rolService.crear(request));
        assertTrue(ex.getMessage().contains("Rol existente"));
    }

    @Test
    @DisplayName("Modificar rol exitosamente")
    void testModificarRol() {
        RolRequest request = new RolRequest();
        request.setRol("ADMINISTRADOR");
        request.setRolDescripcion("Administrador modificado");

        Optional<RolDTO> rolModificado = rolService.modificar(request);

        assertTrue(rolModificado.isPresent());
        assertEquals("ADMINISTRADOR", rolModificado.get().rol());
        assertEquals("Administrador modificado", rolModificado.get().rolDescripcion());

        // Verificar en BD
        Optional<Rol> enBD = rolRepository.buscarTipoRol("ADMINISTRADOR");
        assertTrue(enBD.isPresent());
        assertEquals("Administrador modificado", enBD.get().getDescripcion());
    }

    @Test
    @DisplayName("Modificar rol inexistente lanza excepción")
    void testModificarRolInexistente() {
        RolRequest request = new RolRequest();
        request.setRol("NO_EXISTE");
        request.setRolDescripcion("Descripción");

        RolNotFoundException ex = assertThrows(RolNotFoundException.class, () -> rolService.modificar(request));
        assertTrue(ex.getMessage().contains("Rol inexistente"));
    }

    @Test
    @DisplayName("Eliminar rol exitosamente")
    void testEliminarRol() {
        rolService.eliminar("ADMINISTRADOR");

        Optional<Rol> enBD = rolRepository.buscarTipoRol("ADMINISTRADOR");
        assertTrue(enBD.isEmpty());
    }

    @Test
    @DisplayName("Eliminar rol inexistente lanza excepción")
    void testEliminarRolInexistente() {
        RolNotFoundException ex = assertThrows(RolNotFoundException.class, () -> rolService.eliminar("NO_EXISTE"));
        assertTrue(ex.getMessage().contains("Rol inexistente"));
    }
}

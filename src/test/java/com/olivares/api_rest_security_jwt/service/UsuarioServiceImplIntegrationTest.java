package com.olivares.api_rest_security_jwt.service;

import com.olivares.api_rest_security_jwt.exception.UsuarioNotFoundException;
import com.olivares.api_rest_security_jwt.model.dto.request.UsuarioRequest;
import com.olivares.api_rest_security_jwt.model.dto.response.UsuarioDTO;
import com.olivares.api_rest_security_jwt.model.entity.Usuario;
import com.olivares.api_rest_security_jwt.model.enumerated.Genero;
import com.olivares.api_rest_security_jwt.model.enumerated.TipoDocumento;
import com.olivares.api_rest_security_jwt.repository.UsuarioRepository;
import lombok.extern.log4j.Log4j2;
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

@Log4j2
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class UsuarioServiceImplIntegrationTest {

    @Autowired
    private UsuarioServiceImpl usuarioService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private Usuario usuarioExistente;

    @BeforeEach
    void setUp() {
        // Limpiar la base de datos antes de cada prueba
        usuarioRepository.deleteAll();

        // Crear datos de prueba
        usuarioExistente = new Usuario();
        usuarioExistente.setNombres("Carlos");
        usuarioExistente.setApellidos("Ramírez");
        usuarioExistente.setGenero(Genero.MASCULINO);
        usuarioExistente.setTipoDocumento(TipoDocumento.CEDULA);
        usuarioExistente.setNumeroDocumento("12345678");
        usuarioExistente.setEmail("carlos@example.com");
        usuarioExistente.setPassword("12345");
        usuarioExistente.setTelefono("123456789");

        usuarioRepository.saveAndFlush(usuarioExistente);
    }

    @Test
    @Order(1)
    @DisplayName("Test de listado de usaurios.")
    void testListadoPorPaginacion() {
        int pageNo = 0;
        int pageSize = 10;

        List<UsuarioDTO> usuarios = usuarioService.listadoPorPaginacion(pageNo, pageSize);

        assertNotNull(usuarios);
        assertEquals(1, usuarios.size());
        assertEquals("Carlos", usuarios.get(0).nombres());
    }

    @Test
    @Order(2)
    @DisplayName("Test crear usuario con email existente lanza excepción")
    void testCrearUsuarioEmailExistente() {
        UsuarioRequest request = new UsuarioRequest();
        request.setEmail(usuarioExistente.getEmail()); // mismo email que usuario ya guardado

        UsuarioNotFoundException exception = assertThrows(UsuarioNotFoundException.class, () -> {
            usuarioService.crear(request);
        });

        assertTrue(exception.getMessage().contains("Usuario existente"));
    }

    @Test
    @Order(3)
    @DisplayName("Test modificar usuario exitosamente")
    void testModificarUsuario() {
        UsuarioRequest request = new UsuarioRequest();
        request.setNombres("Carlos Oswaldo");
        request.setApellidos(usuarioExistente.getApellidos());
        request.setGenero(usuarioExistente.getGenero());
        request.setTipoDocumento(usuarioExistente.getTipoDocumento());
        request.setNumeroDocumento(usuarioExistente.getNumeroDocumento());
        request.setEmail(usuarioExistente.getEmail());
        request.setPassword("nuevaClave");
        request.setTelefono("1174857498");

        Optional<UsuarioDTO> usuarioModificado = usuarioService.modificar(request);

        assertTrue(usuarioModificado.isPresent());
        assertEquals("Carlos Oswaldo", usuarioModificado.get().nombres());

        // Verificar que el cambio se guardó en BD
        log.info("Número Documento: {}", usuarioExistente.getNumeroDocumento());

        Optional<Usuario> enBD = usuarioRepository.buscarPorDocumento(usuarioExistente.getTipoDocumento(), usuarioExistente.getNumeroDocumento());
        assertTrue(enBD.isPresent());
        assertEquals("Carlos Oswaldo", enBD.get().getNombres());
        assertEquals("nuevaClave", enBD.get().getPassword());
    }

    @Test
    @Order(4)
    @DisplayName("Test modificar usuario inexistente lanza excepción")
    void testModificarUsuarioNoExiste() {
        UsuarioRequest request = new UsuarioRequest();
        request.setEmail("noexiste@example.com");

        UsuarioNotFoundException exception = assertThrows(UsuarioNotFoundException.class, () -> {
            usuarioService.modificar(request);
        });

        assertTrue(exception.getMessage().contains("Usuario no existente"));
    }

    @Test
    @Order(5)
    @DisplayName("Test eliminar usuario exitosamente")
    void testEliminarUsuario() {
        assertDoesNotThrow(() -> {
            usuarioService.eliminar(usuarioExistente.getTipoDocumento(), usuarioExistente.getNumeroDocumento());
        });

        // Verificar que el usuario ya no existe
        Optional<Usuario> enBD = usuarioRepository.buscarPorDocumento(usuarioExistente.getTipoDocumento(), usuarioExistente.getNumeroDocumento());
        assertTrue(enBD.isEmpty());
    }

    @Test
    @Order(6)
    @DisplayName("Test eliminar usuario inexistente lanza excepción")
    void testEliminarUsuario_NoExiste() {
        UsuarioNotFoundException exception = assertThrows(UsuarioNotFoundException.class, () -> {
            usuarioService.eliminar(TipoDocumento.PASAPORTE, "noexistente123");
        });

        assertTrue(exception.getMessage().contains("no existe"));
    }


}
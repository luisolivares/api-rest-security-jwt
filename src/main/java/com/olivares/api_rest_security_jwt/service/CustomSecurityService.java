package com.olivares.api_rest_security_jwt.service;

import com.olivares.api_rest_security_jwt.repository.RolRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;

@Log4j2
@Component("customSecurityService")
@RequiredArgsConstructor
public class CustomSecurityService {

    private final RolRepository rolRepository;

    /**
     * Verifica si el usuario autenticado tiene al menos un rol.
     */
    public boolean usuarioTieneRol() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        List<String> rolesBD = rolRepository.findAll().stream()
                .map(rol -> "ROLE_" + rol.getTipoRol())
                .toList();

        List<String> rolesUsuario = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        log.info("Roles del usuario: {}", rolesUsuario);
        log.info("Roles en BD: {}", rolesBD);

        boolean usuarioTieneRol = rolesUsuario.stream().anyMatch(rolesBD::contains);

        return usuarioTieneRol;


    }
}

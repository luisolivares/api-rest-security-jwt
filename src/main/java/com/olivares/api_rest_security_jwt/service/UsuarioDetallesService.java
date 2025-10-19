package com.olivares.api_rest_security_jwt.service;

import com.olivares.api_rest_security_jwt.model.entity.Usuario;
import com.olivares.api_rest_security_jwt.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor
public class UsuarioDetallesService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional<Usuario> usuarioOptional = usuarioRepository.buscarPorEmail(username);

        if (usuarioOptional.isEmpty()) {
            log.error("Usuario no encontrado");
            throw new UsernameNotFoundException("Usuario no encontrado");
        }

        Usuario usuario = usuarioOptional.get();

        List<SimpleGrantedAuthority> roles = usuario.getRoles().stream()
                .map(rol -> new SimpleGrantedAuthority("ROLE_" + rol.getTipoRol()))
                .toList();

        return User.builder()
                .username(usuario.getEmail())
                .password(usuario.getPassword())
                .authorities(roles)
                .build();
    }


}
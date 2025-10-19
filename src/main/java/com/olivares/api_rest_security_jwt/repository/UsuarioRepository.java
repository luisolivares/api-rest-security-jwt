package com.olivares.api_rest_security_jwt.repository;

import com.olivares.api_rest_security_jwt.model.entity.Usuario;
import com.olivares.api_rest_security_jwt.model.enumerated.TipoDocumento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    @Query("SELECT u FROM Usuario u WHERE u.email = :email")
    Optional<Usuario> buscarPorEmail(@Param("email") String email);

    @Query(value = "SELECT u FROM Usuario u WHERE u.tipoDocumento = :tipoDocumento AND u.numeroDocumento = :documento ")
    Optional<Usuario> buscarPorDocumento(@Param("tipoDocumento") TipoDocumento tipoDocumento, @Param("documento") String documento);

    @Modifying
    @Query(value = "DELETE FROM Usuario u WHERE u.tipoDocumento = :tipoDocumento AND u.numeroDocumento = :numeroDocumento  ")
    void eliminarPorTipoYNumeroDocumento(@Param("tipoDocumento") TipoDocumento tipoDocumento, @Param("numeroDocumento") String numeroDocumento);

}

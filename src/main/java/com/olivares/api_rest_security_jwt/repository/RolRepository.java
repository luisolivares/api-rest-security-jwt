package com.olivares.api_rest_security_jwt.repository;

import com.olivares.api_rest_security_jwt.model.entity.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RolRepository extends JpaRepository<Rol, Long> {

    @Query("SELECT r FROM Rol r WHERE r.tipoRol = :tipoRol")
    Optional<Rol> buscarTipoRol(@Param("tipoRol") String tipoRol);

}

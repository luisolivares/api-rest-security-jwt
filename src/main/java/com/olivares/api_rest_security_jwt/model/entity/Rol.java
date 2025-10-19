package com.olivares.api_rest_security_jwt.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "Rol")
@Table(name = "rol")
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El campo tipoRol no debe ser vacio")
    @NotNull(message = "El campo tipoRol no debe ser nulo")
    @Column(name = "tipoRol", unique = true, nullable = false)
    private String tipoRol;

    @Column(name = "descripcion", nullable = true)
    private String descripcion;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

}

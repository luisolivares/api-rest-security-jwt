package com.olivares.api_rest_security_jwt.model.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.olivares.api_rest_security_jwt.model.enumerated.Genero;
import com.olivares.api_rest_security_jwt.model.enumerated.TipoDocumento;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "Usuario")
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El campo nombres no debe estar vacio")
    @NotNull(message = "El campo nombres no debe ser nulo")
    @Column(name = "nombres")
    private String nombres;

    @NotBlank(message = "El campo apellidos no debe estar vacio")
    @NotNull(message = "El campo apellidos no debe ser nulo")
    @Column(name = "apellidos")
    private String apellidos;

    @NotNull(message = "El campo tipoDocumento no debe ser nulo")
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_documento", nullable = false)
    private TipoDocumento tipoDocumento;

    @NotBlank(message = "El campo numeroDocumento no debe estar vacio")
    @NotNull(message = "El campo numeroDocumento no debe ser nulo")
    @Column(name = "numero_documento", unique = true, nullable = false)
    private String numeroDocumento;

    @NotNull(message = "El campo genero no debe ser nulo")
    @Enumerated(EnumType.STRING)
    @Column(name = "genero", nullable = false)
    private Genero genero;

    @NotBlank(message = "El campo email no debe estar vacio")
    @NotNull(message = "El campo email no debe ser nulo")
    @Email(message = "El formato del email es inválido")
    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @NotBlank(message = "El campo password no debe estar vacio")
    @NotNull(message = "El campo password no debe ser nulo")
    @Column(name = "password", nullable = false)
    private String password;

    @NotBlank(message = "El campo email no debe estar vacio")
    @NotNull(message = "El campo email no debe ser nulo")
    @Column(name = "telefono", nullable = false)
    private String telefono;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonManagedReference
    private Set<Rol> roles = new HashSet<>();

}
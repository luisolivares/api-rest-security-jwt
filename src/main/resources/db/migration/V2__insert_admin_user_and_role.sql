INSERT INTO usuario (id, nombres, apellidos, tipo_documento, numero_documento, genero, email, password, telefono)
VALUES (
    1,
    'Juan Carlos',
    'Ramírez Torres',
    'CEDULA',
    '95955365',
    'MASCULINO',
    'email@email.com',
    '$2a$10$JteX3XelvUvnVIbKduJ.lOAgBsC/y/xTAxkjMMEdIz1...', -- contraseña encriptada
    '987654321'
);

INSERT INTO rol (id, tipo_rol, descripcion, usuario_id)
VALUES (
    1,
    'ADMINISTRADOR',
    'Administrador con acceso total al sistema',
    1
);

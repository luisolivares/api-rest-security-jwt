# ?? Proyecto JWT con Spring Boot y MySQL

Este proyecto es una **API REST** desarrollada con **Spring Boot 3** con **Spring Security**, que implementa **autenticaci��n y autorizaci��n basada en JWT (JSON Web Token)**.  
Se ejecuta completamente en contenedores Docker, utilizando **MySQL** como base de datos y **phpMyAdmin** para la administraci��n visual.

---

## ?? Tecnolog��as utilizadas

- **Java 17**
- **Spring Boot 3**
- **Spring Security + JWT**
- **Maven**
- **MySQL 8**
- **H2**
- **phpMyAdmin**
- **Docker & Docker Compose**

---

## ?? Requisitos previos

Solo necesitas tener instalados en tu m��quina:

- [Docker](https://www.docker.com/)
- [Docker Compose](https://docs.docker.com/compose/)

No se requiere tener instalado Java, Maven y MySQL localmente.

---

## ?? C��mo ejecutar el proyecto

1. Clona o descarga este repositorio.
2. En la ra��z del proyecto (donde est�� el archivo `docker-compose.yml`), ejecuta:

```bash
docker compose up --build
```


3. Espera a que Docker descargue las im��genes y construya los contenedores.

4. Una vez iniciado, la API estar�� disponible en:

?? http://localhost:8080

Y phpMyAdmin en:

?? http://localhost:8082/phpmyadmin

Usuario: root
Contrase?a: admin

5. Acceder a la aplicaci��n

- **API Base** �� http://localhost:8080/api-rest-security-jwt

- **API Base para verificar funcionamiento** �� http://localhost:8080/api-rest-security-jwt/api/v1/healthz

- **Swagger UI** �� http://localhost:8080/api-rest-security-jwt/swagger-ui/index.html

- **phpMyAdmin** �� http://localhost:8082/phpmyadmin

- **Usuario:** root
- **Contrase?a:** admin

- **Consola H2** �� http://localhost:8080/api-rest-security-jwt/h2-console

- **Driver:** org.h2.Driver

- **URL:** jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE

- **User:** sa

- **Password:** sa

---

## ??? Configuraci��n de la base de datos 

El contenedor de MySQL se levanta autom��ticamente con los siguientes valores:


- **MYSQL_DATABASE:** jwt_app
- **MYSQL_ROOT_PASSWORD:** admin
- **Usuario:** root
- **Puerto:** 3306

Los datos se almacenan en un volumen Docker llamado mysql_data para persistencia.

## ?? Comandos ��tiles

Levantar el entorno:

```bash
docker compose up --build
```

Ver logs en tiempo real:

```bash
docker compose logs -f
```

Detener los contenedores:

```bash
docker compose down
```

Reiniciar el entorno desde cero (incluyendo vol��menes):

```bash
docker compose down -v
docker compose up --build
```

---

## ?? Notas adicionales

El proyecto usa un Dockerfile multietapa: primero compila con Maven y luego ejecuta con JDK 17.

Todos los servicios se ejecutan en una red interna de Docker creada autom��ticamente por Compose.

Si la aplicaci��n define datos iniciales (en data.sql o schema.sql), se cargan al iniciar el contenedor MySQL.

Se creo un usuario inicial cuyo email es **email@email.com** y contrase?a es **admin123**, en el cual se puede generar el primer token del sistema en el endpont de ?? **/api/v1/auth/token**

## ?? Autor 
Proyecto base generado con Spring Boot y configurado por ***Luis Alberto Olivares Pe?a***.

## ?? Versi��n: 1.0.0
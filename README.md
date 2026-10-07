# Reserve One Backend

Backend de la API para la plataforma de reservas, membresías, horarios, pagos y suscripciones del proyecto Lanhua.

## Descripción general

Este servicio está desarrollado con Java 21 y Spring Boot, y expone una API REST para gestionar:

- autenticación y autorización de usuarios
- perfiles de usuario e información complementaria
- catálogos y membresías
- horarios y clases
- reservas
- pagos y suscripciones
- administración de contenido y usuarios por roles

La aplicación principal se encuentra dentro de la carpeta `lanhua/` y usa PostgreSQL como base de datos, JWT para autenticación y Swagger/OpenAPI para documentación.

## Stack tecnológico

- Java 21
- Spring Boot 4.1.1
- Spring Web MVC
- Spring Data JPA
- Spring Security
- PostgreSQL
- JWT (jjwt)
- Lombok
- Springdoc OpenAPI / Swagger UI
- dotenv-java
- Maven

## Estructura del proyecto

```text
reserve-one-backend/
├── .env
├── .gitignore
├── lanhua/
│   ├── Dockerfile
│   ├── mvnw
│   ├── mvnw.cmd
│   ├── pom.xml
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/reserveone/lanhua/
│   │   │   │   ├── config/
│   │   │   │   ├── exception/
│   │   │   │   ├── modules/
│   │   │   │   ├── security/
│   │   │   │   └── shared/
│   │   │   └── resources/application.properties
│   │   └── test/
│   └── target/
└── README.md
```

## Requisitos previos

- Java 21
- Maven 3.9+
- PostgreSQL 14 o superior
- Un archivo `.env` con las variables de entorno necesarias

## Configuración del entorno

Crea un archivo `.env` en la raíz del repositorio con este formato:

```env
DB_HOST=localhost
DB_PORT=5432
DB_NAME=lanhua_db
DB_USER=postgres
DB_PASSWORD=tu_password
JWT_SECRET=tu_clave_secreta_muy_segura
JWT_EXPIRATION=604800000
BOLD_IDENTITY_KEY=tu_identity_key
CORS_ALLOWED_ORIGINS=http://localhost:5173,http://localhost:3000
```

> La app carga este archivo automáticamente mediante `spring.config.import=optional:file:.env[.properties]` y `DotenvConfig.load()`.

## Ejecución local

Desde la carpeta `lanhua/`:

```bash
./mvnw clean install
./mvnw spring-boot:run
```

O en Windows:

```bash
mvnw.cmd clean install
mvnw.cmd spring-boot:run
```

La API quedará disponible por defecto en:

```text
http://localhost:8080
```

## Documentación Swagger

La documentación OpenAPI se expone en:

```text
http://localhost:8080/swagger-ui/index.html
```

También está disponible el JSON de la API en:

```text
http://localhost:8080/v3/api-docs
```

## Seguridad y autenticación

La aplicación usa Spring Security con JWT y política stateless.

### Autenticación
- Endpoint público para login: `POST /api/auth/login`
- Se retorna un token JWT que debe enviarse en el header:

```http
Authorization: Bearer <token>
```

### Autorización
La API define accesos según roles, principalmente:

- usuarios autenticados para reservas y pagos
- administradores para gestión de catálogos, membresías, horarios y usuarios
- rutas abiertas para login, registro de usuarios y documentación

## Roles principales

El sistema contempla permisos de acceso por rol, con prioridad para administración:

- `ADMIN`
- `USER`

## Modulos principales

El backend organiza la lógica por módulos, entre ellos:

- `auth`
- `user`
- `user_information`
- `membership`
- `subscription`
- `catalog`
- `catalog_membership`
- `class_schedule`
- `reservation`
- `payment`

## Docker

El proyecto incluye un `Dockerfile` dentro de `lanhua/` para construir la imagen del backend.

Ejemplo:

```bash
cd lanhua
docker build -t lanhua-backend .
docker run -p 8080:8080 --env-file ../.env lanhua-backend
```

## Buenas prácticas

- Mantener las variables sensibles en `.env` y no en el código fuente
- Verificar la conexión a PostgreSQL antes de levantar la app
- Usar credenciales y secret keys distintas por entorno
- Revisar la documentación de Swagger al probar endpoints nuevos

## Contribución

1. Crear una rama nueva
2. Implementar la mejora o corrección
3. Validar la compilación y pruebas
4. Abrir un pull request con descripción clara

## Autor

Proyecto desarrollado para la plataforma de reservas Reserve One.

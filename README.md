# Microservicio de Usuarios

Arquitectura simple de un microservicio para gestión de usuarios con Spring Boot y Lombok.

## Arquitectura

### Modelos de Datos

- **Roles**: Gestiona los roles del sistema (ADMIN, USER)
- **Usuarios**: Información principal de usuarios
- **TerminosCondiciones**: Versiones de términos y condiciones
- **UsuariosTerminos**: Tabla intermedia para tracking de aceptación de términos

### Capas de la Aplicación

- **Model**: Entidades JPA con Lombok
- **Repository**: Interfaces JpaRepository
- **Service**: Lógica de negocio y CRUD operations
- **Controller**: Endpoints REST API
- **DTO**: Objetos de transferencia de datos

## Endpoints API

### 1. POST `/users/register`
Registra un nuevo usuario en el sistema.

**Request Body:**
```json
{
  "nombre": "Juan Perez",
  "email": "juan@test.com",
  "password": "password123",
  "idRol": 1
}
```

**Response:**
```json
{
  "message": "Usuario registrado exitosamente",
  "usuario": {
    "idUsuario": 1,
    "nombre": "Juan Perez",
    "email": "juan@test.com",
    "rol": {
      "idRol": 1,
      "nombre": "USER"
    }
  }
}
```

### 2. POST `/users/login`
Autentica un usuario y genera un token de sesión.

**Request Body:**
```json
{
  "email": "juan@test.com",
  "password": "password123"
}
```

**Response:**
```json
{
  "message": "Login successful",
  "usuario": {
    "idUsuario": 1,
    "nombre": "Juan Perez",
    "email": "juan@test.com"
  },
  "token": "dummy-token-1"
}
```

### 3. GET `/users/{id}`
Obtiene información detallada de un usuario.

**Response:**
```json
{
  "idUsuario": 1,
  "nombre": "Juan Perez",
  "email": "juan@test.com",
  "rol": {
    "idRol": 1,
    "nombre": "USER"
  }
}
```

### 4. PUT `/users/{id}`
Actualiza datos de un usuario existente.

**Request Body:**
```json
{
  "nombre": "Juan Actualizado",
  "email": "juan.nuevo@test.com"
}
```

### 5. DELETE `/users/{id}`
Elimina un usuario del sistema.

**Response:**
```json
{
  "message": "Usuario eliminado exitosamente"
}
```

## Configuración

### Base de Datos
- **Motor**: MariaDB (base de datos persistente)
- **URL**: jdbc:mariadb://localhost:3306/usuarios_db
- **Setup**: Ver `MariaDB_Setup.md` para configuración completa

### Datos Iniciales
- Roles: ADMIN (id=1), USER (id=2)
- Términos y Condiciones: Versión 1.0

## Ejecución

```bash
# Compilar el proyecto
./mvnw.cmd clean compile

# Ejecutar tests
./mvnw.cmd test

# Iniciar aplicación
./mvnw.cmd spring-boot:run
```

La aplicación estará disponible en http://localhost:8081

## Notas

- La implementación es simple y no incluye seguridad real (JWT, encriptación)
- Los passwords se almacenan en texto plano para simplicidad
- Los tokens son dummy tokens para demostración
- La base de datos MariaDB es persistente (los datos se guardan entre reinicios)
- Se requiere configuración inicial de MariaDB (ver `MariaDB_Setup.md`)

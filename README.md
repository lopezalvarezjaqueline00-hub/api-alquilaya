# API REST AlquilaYA

API REST para una empresa de **alquiler de vehículos**: oficinas, vehículos, clientes y alquileres.
Calcula el precio total del alquiler, impide alquilar un vehículo ocupado o en mantenimiento y
actualiza el estado del vehículo al alquilarlo, devolverlo o cancelar. Sigue las convenciones REST:
recursos en plural, métodos HTTP y códigos de estado adecuados.

## Tecnologías utilizadas

| Tecnología | Uso |
|---|---|
| Java 21 | Lenguaje |
| Spring Boot 4 | Framework (Spring Web MVC, Spring Data JPA, Bean Validation) |
| Hibernate / JPA | Mapeo objeto-relacional; crea las tablas automáticamente |
| MySQL 8 | Base de datos |
| Maven (Maven Wrapper) | Compilación y dependencias; no requiere instalar Maven |
| JUnit 5 + Mockito + H2 | Pruebas automáticas |
| Postman | Pruebas manuales de la API |

## Modelo de datos

```
OficinaAlquiler 1 ──── * Vehiculo 1 ──── * Alquiler * ──── 1 Cliente
```

| Entidad | Campos principales |
|---|---|
| OficinaAlquiler | idOficina, nombre, direccion, ciudad, region |
| Vehiculo | idVehiculo, marca, modelo, anio, matricula (única), tarifaDiaria, estado, oficina |
| Cliente | idCliente, nombre, apellido, email (único), licenciaConducir (única), telefono |
| Alquiler | idAlquiler, vehiculo, cliente, fechaAlquiler, fechaDevolucion, precioTotal, estado |

## Instalación y ejecución

### Requisitos previos

| Herramienta | Versión | Instalación en Windows |
|---|---|---|
| Java JDK | 21 | `winget install EclipseAdoptium.Temurin.21.JDK` |
| MySQL Server | 8.x | `winget install Oracle.MySQL` y configurarlo con **MySQL Configurator** |
| Git | — | `winget install Git.Git` |
| Postman | — | https://www.postman.com/downloads/ |

### Pasos

1. **Clonar el repositorio**
   ```bash
   git clone https://github.com/lopezalvarezjaqueline00-hub/api-alquilaya.git
   cd api-alquilaya
   ```
2. **Encender MySQL** (el servicio `MySQL84` o equivalente debe estar en *Running*).
   La base de datos `alquilaya` y sus tablas **se crean automáticamente** al arrancar.
3. **Configurar las variables de entorno** si tu usuario o contraseña de MySQL son distintos a los
   valores por defecto (ver la sección siguiente).
4. **Ejecutar la aplicación**
   ```bash
   .\mvnw.cmd spring-boot:run     # Windows (PowerShell)
   ./mvnw spring-boot:run         # Mac / Linux
   ```
   La primera ejecución descarga las dependencias. La API está lista cuando aparece
   `Started ...Application` y `Tomcat started on port 8080`.
5. **Comprobar**: abrir `http://localhost:8080/api/vehiculos` en el navegador; debe responder `[]`.
6. **Detener**: `Ctrl + C` en la terminal.

Pruebas automáticas (no necesitan MySQL, usan H2 en memoria): `.\mvnw.cmd test`

## Variables de entorno

Las credenciales **no están escritas en el código**: `application.properties` las lee de variables
de entorno. Si una variable no existe se usa el valor por defecto, pensado para desarrollo local.

| Variable | Descripción | Valor por defecto |
|---|---|---|
| `DB_URL` | URL JDBC de MySQL | `jdbc:mysql://localhost:3306/alquilaya?createDatabaseIfNotExist=true` |
| `DB_USERNAME` | Usuario de MySQL | `root` |
| `DB_PASSWORD` | Contraseña de MySQL | `root1234` |
| `SERVER_PORT` | Puerto de la API | `8080` |

Ejemplo para definirlas antes de ejecutar (PowerShell, solo para esa terminal):

```powershell
$env:DB_USERNAME = "root"
$env:DB_PASSWORD = "mi_contraseña"
.\mvnw.cmd spring-boot:run
```

## Endpoints

### Oficinas `/api/oficinas`
| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/oficinas` | Listar oficinas |
| GET | `/api/oficinas/{id}` | Oficina por ID |
| GET | `/api/oficinas/ciudad/{ciudad}` | Oficinas de una ciudad |
| GET | `/api/oficinas/{id}/vehiculos` | Vehículos de una oficina |
| POST | `/api/oficinas` | Crear |
| PUT | `/api/oficinas/{id}` | Actualizar |
| DELETE | `/api/oficinas/{id}` | Eliminar |

### Vehículos `/api/vehiculos`
| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/vehiculos` | Listar vehículos |
| GET | `/api/vehiculos/{id}` | Vehículo por ID |
| GET | `/api/vehiculos/matricula/{matricula}` | Vehículo por matrícula |
| GET | `/api/vehiculos/estado/{estado}` | Por estado: DISPONIBLE, ALQUILADO, MANTENIMIENTO |
| POST | `/api/vehiculos` | Crear |
| PUT | `/api/vehiculos/{id}` | Actualizar |
| DELETE | `/api/vehiculos/{id}` | Eliminar |

### Clientes `/api/clientes`
| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/clientes` | Listar clientes |
| GET | `/api/clientes/{id}` | Cliente por ID |
| GET | `/api/clientes/email/{email}` | Cliente por email |
| GET | `/api/clientes/licencia/{licencia}` | Cliente por licencia |
| POST | `/api/clientes` | Crear |
| PUT | `/api/clientes/{id}` | Actualizar |
| DELETE | `/api/clientes/{id}` | Eliminar |

### Alquileres `/api/alquileres`
| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/alquileres` | Listar alquileres |
| GET | `/api/alquileres/{id}` | Alquiler por ID |
| GET | `/api/alquileres/cliente/{idCliente}` | Alquileres de un cliente |
| GET | `/api/alquileres/vehiculo/{idVehiculo}` | Alquileres de un vehículo |
| GET | `/api/alquileres/estado/{estado}` | Por estado: ACTIVO, FINALIZADO, CANCELADO |
| POST | `/api/alquileres` | Registrar alquiler |
| PUT | `/api/alquileres/{id}` | Actualizar |
| PATCH | `/api/alquileres/{id}/finalizar` | Devolución del vehículo |
| PATCH | `/api/alquileres/{id}/cancelar` | Cancelar |
| DELETE | `/api/alquileres/{id}` | Eliminar |

## Ejemplos de peticiones y respuestas

### Crear un vehículo

**Petición**

```http
POST /api/vehiculos
Content-Type: application/json

{
  "marca": "Nissan",
  "modelo": "Versa",
  "anio": 2024,
  "matricula": "TXA-123-A",
  "tarifaDiaria": 650.00,
  "estado": "DISPONIBLE",
  "oficina": { "idOficina": 1 }
}
```

**Respuesta** `201 Created`

```json
{
  "idVehiculo": 1,
  "marca": "Nissan",
  "modelo": "Versa",
  "anio": 2024,
  "matricula": "TXA-123-A",
  "tarifaDiaria": 650.00,
  "estado": "DISPONIBLE",
  "oficina": {
    "idOficina": 1,
    "nombre": "AlquilaYA Centro",
    "direccion": "Av. Reforma 123, Col. Centro",
    "ciudad": "Puebla",
    "region": "Centro"
  }
}
```

### Registrar un alquiler (el precio se calcula: 3 días × 650)

**Petición**

```http
POST /api/alquileres
Content-Type: application/json

{
  "vehiculo": { "idVehiculo": 1 },
  "cliente": { "idCliente": 1 },
  "fechaAlquiler": "2026-10-15",
  "fechaDevolucion": "2026-10-18"
}
```

**Respuesta** `201 Created`

```json
{
  "idAlquiler": 1,
  "vehiculo": { "idVehiculo": 1, "marca": "Nissan", "modelo": "Versa", "estado": "ALQUILADO", "...": "..." },
  "cliente": { "idCliente": 1, "nombre": "Ana", "apellido": "Pérez", "email": "ana.perez@correo.com", "...": "..." },
  "fechaAlquiler": "2026-10-15",
  "fechaDevolucion": "2026-10-18",
  "precioTotal": 1950.00,
  "estado": "ACTIVO"
}
```

### Error de validación: vehículo ocupado en esas fechas

**Petición**

```http
POST /api/alquileres
Content-Type: application/json

{
  "vehiculo": { "idVehiculo": 1 },
  "cliente": { "idCliente": 1 },
  "fechaAlquiler": "2026-10-17",
  "fechaDevolucion": "2026-10-20"
}
```

**Respuesta** `400 Bad Request`

```json
{
  "timestamp": "2026-10-13T17:02:44.120",
  "status": 400,
  "error": "El vehículo ya está alquilado en esas fechas"
}
```

### Error de validación: email inválido

**Petición**

```http
POST /api/clientes
Content-Type: application/json

{
  "nombre": "Luis",
  "apellido": "Gómez",
  "email": "esto-no-es-un-email",
  "licenciaConducir": "PUE-7654321"
}
```

**Respuesta** `400 Bad Request`

```json
{
  "timestamp": "2026-10-13T17:03:10.551",
  "status": 400,
  "error": "El email no es válido"
}
```

### Conflicto: eliminar una oficina con vehículos

**Petición**

```http
DELETE /api/oficinas/1
```

**Respuesta** `409 Conflict`

```json
{
  "timestamp": "2026-10-13T17:05:31.009",
  "status": 409,
  "error": "La operación viola una restricción de la base de datos (dato duplicado o registro relacionado)"
}
```

## Reglas de negocio

- La matrícula del vehículo, el email y la licencia del cliente no se pueden repetir.
- Al alquilar: el vehículo y el cliente deben existir, la fecha de devolución debe ser posterior a la de alquiler,
  el vehículo no puede estar en `MANTENIMIENTO` ni tener otro alquiler `ACTIVO` en esas fechas.
- `precioTotal` lo calcula el servidor: `tarifaDiaria × días`.
- Al registrar un alquiler el vehículo pasa a `ALQUILADO`; al finalizarlo o cancelarlo vuelve a `DISPONIBLE`.
- Los errores se responden en JSON: `{ "timestamp": ..., "status": 400, "error": "mensaje" }`.

## Códigos de respuesta y errores

| Código | Cuándo |
|---|---|
| `200 OK` | Consulta, actualización o acción correcta |
| `201 Created` | Registro creado |
| `204 No Content` | Registro eliminado |
| `400 Bad Request` | Datos inválidos o regla de negocio no cumplida |
| `404 Not Found` | El registro solicitado no existe |
| `409 Conflict` | Restricción de la base de datos (dato duplicado o registro relacionado) |

Los errores 400 y 409 devuelven un JSON con el motivo:

```json
{ "timestamp": "2026-10-13T16:37:50.935", "status": 400, "error": "Mensaje que explica el error" }
```

## Pruebas con Postman

1. Abrir Postman → **Import** → seleccionar `docs/API_AlquilaYA.postman_collection.json`.
2. La colección define la variable `baseUrl` = `http://localhost:8080`.
3. Ejecutar las peticiones **en orden**, carpeta por carpeta, con **Send**.
4. Las peticiones cuyo nombre dice *"debe responder 400 / 404 / 409"* prueban el manejo de errores.

Alternativa: extensión **REST Client** de VS Code con `docs/pruebas-api.http`.

## Estructura del proyecto

```
src/main/java/com/api/alquilaya/
├── controllers/   → endpoints REST: reciben la petición y devuelven ResponseEntity
├── services/      → reglas de negocio y validaciones
├── repositories/  → acceso a datos con Spring Data JPA
└── models/        → entidades JPA (tablas)
src/main/resources/application.properties   → puerto y conexión a MySQL (variables de entorno)
src/test/                                    → pruebas unitarias y de arranque
docs/                                        → colección de Postman, archivo .http y JSON de ejemplo
```

## Autor

**Jaqueline López Alvarez** — Ingeniería en Sistemas Computacionales
Asignatura: Arquitectura Orientada a Servicios · Parcial 1

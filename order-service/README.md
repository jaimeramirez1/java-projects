# 🧾 Order Service - Microservicio de Gestión de Pedidos

Microservicio RESTful desarrollado con **Spring Boot 3** para la gestión de pedidos, con arquitectura hexagonal, persistencia en **MongoDB**, caché con **Redis**, y mensajería asíncrona mediante **Apache Kafka**.

## 📋 Tabla de Contenidos
- [Descripción](#-descripción)
- [Arquitectura](#-arquitectura)
- [Tecnologías](#-tecnologías)
- [Requisitos Previos](#-requisitos-previos)
- [Ejecución con Docker Compose](#-ejecución-con-docker-compose)
- [Endpoints y Ejemplos de Uso](#-endpoints-y-ejemplos-de-uso)
- [Decisiones Técnicas](#-decisiones-técnicas)

---

## 📖 Descripción

Order Service es un microservicio que permite:
- ✅ Crear pedidos con información de cliente y productos
- ✅ Consultar pedidos por ID o filtros (estado, cliente)
- ✅ Actualizar el estado de pedidos
- ✅ Cachear pedidos en Redis para optimizar consultas
- ✅ Publicar eventos de cambio de estado a Kafka

---

## 🏗️ Arquitectura

El proyecto sigue una **arquitectura hexagonal (Ports & Adapters)**:

```
order-service/
├── domain/              # Entidades y lógica de negocio
│   ├── model/          # Order (entidad)
│   └── repository/     # OrderRepository (puerto)
├── application/         # Casos de uso
│   └── service/        # OrderService
├── infrastructure/      # Adaptadores
│   ├── persistence/    # MongoOrderRepositoryAdapter
│   ├── messaging/      # KafkaOrderPublisher
│   └── config/         # Configuraciones (Redis, Mongo, Kafka)
└── api/                # Controladores REST
    └── OrderController
```

**Flujo de datos:**
1. Cliente HTTP → `OrderController` → `OrderService`
2. `OrderService` → `OrderRepository` → MongoDB
3. Cache automático en Redis (mediante `@Cacheable`)
4. Eventos publicados a Kafka cuando cambia el estado

---

## 🚀 Tecnologías

| Tecnología | Versión | Propósito |
|------------|---------|-----------|
| Java | 21+ | Lenguaje de programación |
| Spring Boot | 3.x | Framework principal |
| MongoDB | 6.0 | Base de datos NoSQL |
| Redis | 7 | Sistema de caché |
| Apache Kafka | 7.3.0 | Mensajería asíncrona |
| Zookeeper | 7.3.0 | Coordinación de Kafka |
| Maven/Gradle | - | Gestión de dependencias |
| Docker | - | Contenedorización |

---

## 📦 Requisitos Previos

- **Docker** y **Docker Compose** instalados
- **Java 21+** (solo para ejecución local sin Docker)
- **Maven** o **Gradle** (solo para ejecución local)

---

## 🐳 Ejecución con Docker Compose

### 1. Clonar el repositorio
```bash
git clone https://github.com/jaimeramirez1/java-projects.git
cd java-projects/order-service

```

### 2. Iniciar todos los servicios
```bash
docker-compose up -d
```

Esto levantará:
- ✅ MongoDB (puerto 27017)
- ✅ Redis (puerto 6379)
- ✅ Zookeeper (puerto 2181)
- ✅ Kafka (puertos 9092, 9093)
- ✅ Order Service (puerto 8080)

### 3. Verificar que los servicios estén corriendo
```bash
docker-compose ps
```

### 4. Ver logs del servicio
```bash
docker-compose logs -f order-service
```

### 5. Detener los servicios
```bash
docker-compose down
```

Para eliminar también los volúmenes (datos):
```bash
docker-compose down -v
```

---

## 🔗 Endpoints y Ejemplos de Uso

### 📌 Base URL
```
http://localhost:8080
```

---

### 1️⃣ **Crear una nueva orden** (POST)

**Endpoint:** `POST /orders`

**Request:**
```bash
curl -X POST http://localhost:8080/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "123",
    "status": "NEW",
    "items": [
      {"sku": "A1", "quantity": 2, "price": 100},
      {"sku": "B3", "quantity": 1, "price": 50}
    ]
  }'
```

**Response (201 Created):**
```json
{
  "id": "68fc213d22d6844e7939e591",
  "customerId": "123",
  "status": "NEW",
  "items": [
    {"sku": "A1", "quantity": 2, "price": 100},
    {"sku": "B3", "quantity": 1, "price": 50}
  ],
  "createdAt": "2025-10-25T01:00:45Z",
  "updatedAt": "2025-10-25T01:00:45Z"
}
```

---

### 2️⃣ **Consultar orden por ID** (GET)

**Endpoint:** `GET /orders/{id}`

**Request:**
```bash
curl -X GET http://localhost:8080/orders/68fc213d22d6844e7939e591
```

**Response (200 OK):**
```json
{
  "id": "68fc213d22d6844e7939e591",
  "customerId": "123",
  "status": "NEW",
  "items": [
    {"sku": "A1", "quantity": 2, "price": 100},
    {"sku": "B3", "quantity": 1, "price": 50}
  ],
  "createdAt": "2025-10-25T01:00:45Z",
  "updatedAt": "2025-10-25T01:00:45Z"
}
```

> **Nota:** Este endpoint usa caché de Redis. La segunda llamada será más rápida.

---

### 3️⃣ **Listar órdenes filtradas por estado y cliente** (GET)

**Endpoint:** `GET /orders?status={status}&customerId={customerId}`

**Request:**
```bash
curl -X GET "http://localhost:8080/orders?status=NEW&customerId=123"
```

**Response (200 OK):**
```json
[
  {
    "id": "68fc213d22d6844e7939e591",
    "customerId": "123",
    "status": "NEW",
    "items": [
      {"sku": "A1", "quantity": 2, "price": 100},
      {"sku": "B3", "quantity": 1, "price": 50}
    ],
    "createdAt": "2025-10-25T01:00:45Z",
    "updatedAt": "2025-10-25T01:00:45Z"
  }
]
```

---

### 4️⃣ **Cambiar el estado de una orden** (PATCH)

**Endpoint:** `PATCH /orders/{id}/status?newStatus={status}`

**Request:**
```bash
curl -X PATCH "http://localhost:8080/orders/68fc213d22d6844e7939e591/status?newStatus=DELIVERED"
```

**Response (200 OK):**
```json
{
  "id": "68fc213d22d6844e7939e591",
  "customerId": "123",
  "status": "DELIVERED",
  "items": [
    {"sku": "A1", "quantity": 2, "price": 100},
    {"sku": "B3", "quantity": 1, "price": 50}
  ],
  "createdAt": "2025-10-25T01:00:45Z",
  "updatedAt": "2025-10-25T01:15:00Z"
}
```

```

---

## 🧠 Decisiones Técnicas

### 1. **Arquitectura Hexagonal**
- **Decisión:** Separar el dominio de la infraestructura mediante ports & adapters.
- **Motivo:** Facilita el testing, mantenimiento y cambio de tecnologías sin afectar la lógica de negocio.

### 2. **MongoDB como base de datos**
- **Decisión:** Usar MongoDB en lugar de bases de datos relacionales.
- **Motivo:** 
  - Esquema flexible para pedidos con items dinámicos
  - Alto rendimiento en operaciones de lectura/escritura
  - Escalabilidad horizontal nativa

### 3. **Redis para caché**
- **Decisión:** Implementar caché con `@Cacheable` y `@CacheEvict`.
- **Motivo:**
  - Reducir latencia en consultas frecuentes de pedidos
  - Disminuir carga en MongoDB
  - TTL de 1 hora para mantener datos frescos

### 4. **Kafka para eventos**
- **Decisión:** Publicar eventos de cambio de estado a Kafka.
- **Motivo:**
  - Desacoplamiento de servicios (otros servicios pueden reaccionar a cambios)
  - Procesamiento asíncrono
  - Auditoría y trazabilidad de cambios

### 5. **Serialización JSON para Redis**
- **Decisión:** Usar `GenericJackson2JsonRedisSerializer` con `JavaTimeModule`.
- **Motivo:**
  - Soporte nativo para tipos de Java 8+ (Instant, LocalDateTime)
  - Legibilidad de datos en Redis
  - Compatibilidad con objetos complejos

### 6. **Health Checks en Docker**
- **Decisión:** Implementar health checks para MongoDB y Redis.
- **Motivo:**
  - Asegurar que las dependencias estén listas antes de iniciar la aplicación
  - Evitar errores de conexión al arrancar
  - Mejor experiencia con `docker-compose`

### 7. **Perfiles de Spring (local vs docker)**
- **Decisión:** Usar `application.yml` para local y `application-docker.yml` para contenedores.
- **Motivo:**
  - Flexibilidad para desarrollar localmente sin Docker
  - Configuración específica por entorno
  - Facilita pruebas locales

### 8. **Timeouts configurables**
- **Decisión:** Configurar timeouts de conexión de 60 segundos.
- **Motivo:**
  - Dar tiempo suficiente para que los servicios inicien en Docker
  - Evitar falsos negativos en redes lentas
  - Balance entre resiliencia y detección rápida de fallos

---

## 🔍 Verificación de Kafka

Para verificar que los eventos se publican correctamente:

```bash
# Conectarse al contenedor de Kafka
docker-compose exec kafka bash

# Consumir mensajes del tópico
kafka-console-consumer --bootstrap-server localhost:9092 \
  --topic order-status-changed \
  --from-beginning
```

---

## 📊 Monitoreo

### Health Check
```bash
curl http://localhost:8080/actuator/health
```

### Métricas de MongoDB
```bash
curl http://localhost:8080/actuator/health/mongo
```

### Métricas de Redis
```bash
curl http://localhost:8080/actuator/health/redis
```

---

## 🤝 Contribución

1. Fork el proyecto
2. Crea una rama para tu feature (`git checkout -b feature/nueva-funcionalidad`)
3. Commit tus cambios (`git commit -am 'Agrega nueva funcionalidad'`)
4. Push a la rama (`git push origin feature/nueva-funcionalidad`)
5. Abre un Pull Request

---

## 📝 Licencia

Este proyecto está bajo la Licencia MIT. Ver `LICENSE` para más detalles.

---

## 👥 Autor

**Jaime Ramirez** - [GitHub](https://github.com/jaimeramirez1/java-projects)

---

## 📚 Referencias

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [MongoDB Documentation](https://docs.mongodb.com/)
- [Redis Documentation](https://redis.io/documentation)
- [Apache Kafka Documentation](https://kafka.apache.org/documentation/)
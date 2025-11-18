# Tasks API - Microservicio de Gestión de Tareas

## 📋 Descripción
Microservicio para la gestión de tareas, mensajes y notificaciones del sistema I-Wellness.

## 🚀 Despliegue con Docker

### Prerrequisitos
- Docker y Docker Compose instalados
- Red `tesisNetwork` creada (si no existe, créala con: `docker network create tesisNetwork`)
- RabbitMQ corriendo en la red `tesisNetwork`

### Variables de Entorno
Copia el archivo `.env.example` a `.env` y ajusta los valores según tu entorno:

```bash
cp .env.example .env
```

### Construcción y Despliegue

**Opción 1: Usar docker-compose (recomendado)**
```bash
# Construir y levantar el servicio
docker-compose up -d --build

# Ver logs
docker-compose logs -f

# Detener el servicio
docker-compose down
```

**Opción 2: Desde el directorio raíz de la tesis**
```bash
# Si hay un docker-compose.yml principal
docker-compose up -d tasks-api --build
```

### Verificación
```bash
# Verificar que el contenedor está corriendo
docker ps | grep tasks-api

# Verificar health check
curl http://localhost:8091/api/tasks

# Ver logs en tiempo real
docker logs -f tasks-api
```

## 🔌 Endpoints Principales

### Tasks
- `GET /api/tasks` - Listar todas las tareas
- `GET /api/tasks/{id}` - Obtener detalle de una tarea
- `POST /api/tasks` - Crear nueva tarea
- `PUT /api/tasks/{id}` - Actualizar tarea
- `DELETE /api/tasks/{id}` - Eliminar tarea

### Messages
- `GET /api/tasks/{taskId}/messages` - Obtener mensajes de una tarea
- `POST /api/tasks/{taskId}/messages` - Enviar mensaje

### Notifications
- `GET /api/notifications` - Listar notificaciones
- `GET /api/notifications/user/{userId}` - Notificaciones por usuario

## 🗄️ Base de Datos
- **Tipo**: H2 (archivo persistente)
- **Ubicación**: `/app/data/tasksdb` (dentro del contenedor)
- **Volumen Docker**: `tasks_data`
- **Persistencia**: Los datos se mantienen entre reinicios del contenedor

## 🔧 Configuración

### Puerto
- **Puerto local**: 8091
- **Puerto en contenedor**: 8091

### RabbitMQ
El servicio se conecta a RabbitMQ para eventos de tareas:
- Host: `rabbitmq` (nombre del servicio en Docker)
- Puerto: 5672
- Usuario/Contraseña: Configurables en `.env`

## 🐛 Troubleshooting

### El servicio no levanta
```bash
# Ver logs detallados
docker logs tasks-api

# Verificar que la red existe
docker network ls | grep tesisNetwork

# Verificar RabbitMQ
docker ps | grep rabbitmq
```

### Error de conexión a base de datos
```bash
# Verificar permisos del volumen
docker volume inspect tasks_data

# Recrear el volumen si es necesario
docker-compose down -v
docker-compose up -d
```

### Error de memoria
Ajusta los valores de `JAVA_OPTS_XMS` y `JAVA_OPTS_XMX` en el archivo `.env`

## 🔄 Actualización
```bash
# Reconstruir la imagen con cambios
docker-compose up -d --build

# Forzar recreación del contenedor
docker-compose up -d --force-recreate
```

## 📊 Monitoreo
```bash
# Ver estadísticas del contenedor
docker stats tasks-api

# Ver logs con timestamps
docker logs -f --timestamps tasks-api
```

## 🧹 Limpieza
```bash
# Detener y eliminar contenedor (mantiene volumen)
docker-compose down

# Detener y eliminar contenedor y volumen (PRECAUCIÓN: se pierden datos)
docker-compose down -v

# Eliminar imagen
docker rmi tasks-api:latest
```

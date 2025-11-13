#!/bin/bash

# Script de despliegue para Tasks API
# Colores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}================================================${NC}"
echo -e "${BLUE}   Tasks API - Deployment Script${NC}"
echo -e "${BLUE}================================================${NC}"

# Verificar que existe la red tesisNetwork
echo -e "\n${YELLOW}[1/5] Verificando red Docker...${NC}"
if ! docker network ls | grep -q tesisNetwork; then
    echo -e "${YELLOW}Creando red tesisNetwork...${NC}"
    docker network create tesisNetwork
    echo -e "${GREEN}✓ Red creada${NC}"
else
    echo -e "${GREEN}✓ Red tesisNetwork existe${NC}"
fi

# Verificar archivo .env
echo -e "\n${YELLOW}[2/5] Verificando archivo .env...${NC}"
if [ ! -f .env ]; then
    echo -e "${YELLOW}Copiando .env.example a .env...${NC}"
    cp .env.example .env
    echo -e "${GREEN}✓ Archivo .env creado${NC}"
else
    echo -e "${GREEN}✓ Archivo .env existe${NC}"
fi

# Detener contenedor anterior si existe
echo -e "\n${YELLOW}[3/5] Deteniendo contenedor anterior...${NC}"
docker-compose down 2>/dev/null
echo -e "${GREEN}✓ Contenedor detenido${NC}"

# Construir imagen
echo -e "\n${YELLOW}[4/5] Construyendo imagen Docker...${NC}"
docker-compose build --no-cache
if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ Imagen construida exitosamente${NC}"
else
    echo -e "${RED}✗ Error al construir la imagen${NC}"
    exit 1
fi

# Levantar servicio
echo -e "\n${YELLOW}[5/5] Levantando servicio...${NC}"
docker-compose up -d
if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ Servicio iniciado${NC}"
else
    echo -e "${RED}✗ Error al iniciar el servicio${NC}"
    exit 1
fi

# Esperar a que el servicio esté listo
echo -e "\n${YELLOW}Esperando a que el servicio esté listo...${NC}"
sleep 10

# Verificar estado
echo -e "\n${BLUE}================================================${NC}"
echo -e "${BLUE}   Estado del Servicio${NC}"
echo -e "${BLUE}================================================${NC}"

if docker ps | grep -q tasks-api; then
    echo -e "${GREEN}✓ Contenedor corriendo${NC}"
    echo -e "\n${BLUE}Información del contenedor:${NC}"
    docker ps | grep tasks-api
    
    echo -e "\n${BLUE}Health check:${NC}"
    HEALTH_STATUS=$(curl -s http://localhost:8091/api/tasks -o /dev/null -w '%{http_code}')
    if [ "$HEALTH_STATUS" = "200" ]; then
        echo -e "${GREEN}✓ API respondiendo correctamente${NC}"
    else
        echo -e "${YELLOW}⚠ API no responde aún (esto puede tomar unos segundos)${NC}"
    fi
    
    echo -e "\n${BLUE}Puedes ver los logs con:${NC}"
    echo -e "  docker logs -f tasks-api"
    
    echo -e "\n${BLUE}Endpoints disponibles:${NC}"
    echo -e "  ${GREEN}http://localhost:8091/api/tasks${NC}"
    echo -e "  ${GREEN}http://localhost:8091/api/notifications${NC}"
    
    echo -e "\n${GREEN}¡Despliegue completado exitosamente!${NC}"
else
    echo -e "${RED}✗ El contenedor no está corriendo${NC}"
    echo -e "${YELLOW}Ver logs con: docker logs tasks-api${NC}"
    exit 1
fi

echo -e "${BLUE}================================================${NC}"

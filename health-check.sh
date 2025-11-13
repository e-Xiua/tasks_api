#!/bin/bash

# Script para verificar la conectividad del Tasks API
# Colores
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

echo -e "${BLUE}================================================${NC}"
echo -e "${BLUE}   Tasks API - Health Check${NC}"
echo -e "${BLUE}================================================${NC}"

# Verificar contenedor
echo -e "\n${YELLOW}Verificando contenedor Docker...${NC}"
if docker ps | grep -q tasks-api; then
    echo -e "${GREEN}✓ Contenedor tasks-api está corriendo${NC}"
    docker ps | grep tasks-api
else
    echo -e "${RED}✗ Contenedor tasks-api NO está corriendo${NC}"
    exit 1
fi

# Verificar puerto
echo -e "\n${YELLOW}Verificando puerto 8091...${NC}"
if netstat -tuln 2>/dev/null | grep -q ":8091" || ss -tuln 2>/dev/null | grep -q ":8091"; then
    echo -e "${GREEN}✓ Puerto 8091 está en uso${NC}"
else
    echo -e "${RED}✗ Puerto 8091 NO está en uso${NC}"
fi

# Verificar API
echo -e "\n${YELLOW}Verificando API...${NC}"
HTTP_CODE=$(curl -s -o /dev/null -w '%{http_code}' http://localhost:8091/api/tasks)
if [ "$HTTP_CODE" = "200" ]; then
    echo -e "${GREEN}✓ API respondiendo correctamente (HTTP $HTTP_CODE)${NC}"
    
    # Probar obtener tareas
    echo -e "\n${BLUE}Respuesta de la API:${NC}"
    curl -s http://localhost:8091/api/tasks | jq '.' 2>/dev/null || curl -s http://localhost:8091/api/tasks
else
    echo -e "${RED}✗ API no responde correctamente (HTTP $HTTP_CODE)${NC}"
fi

# Ver últimos logs
echo -e "\n${YELLOW}Últimos logs del servicio:${NC}"
docker logs --tail 20 tasks-api

echo -e "\n${BLUE}================================================${NC}"
echo -e "${BLUE}Endpoints disponibles:${NC}"
echo -e "  GET    ${GREEN}http://localhost:8091/api/tasks${NC}"
echo -e "  POST   ${GREEN}http://localhost:8091/api/tasks${NC}"
echo -e "  GET    ${GREEN}http://localhost:8091/api/tasks/{id}${NC}"
echo -e "  PUT    ${GREEN}http://localhost:8091/api/tasks/{id}${NC}"
echo -e "  DELETE ${GREEN}http://localhost:8091/api/tasks/{id}${NC}"
echo -e "  GET    ${GREEN}http://localhost:8091/api/tasks/{id}/messages${NC}"
echo -e "  POST   ${GREEN}http://localhost:8091/api/tasks/{id}/messages${NC}"
echo -e "  GET    ${GREEN}http://localhost:8091/api/notifications${NC}"
echo -e "${BLUE}================================================${NC}"

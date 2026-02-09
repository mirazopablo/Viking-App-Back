#!/bin/bash

# Colores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${GREEN}=== Iniciando script de configuración de entorno para Viking App Back ===${NC}"

# Función para verificar comandos
check_command() {
    if ! command -v "$1" &> /dev/null; then
        return 1
    else
        return 0
    fi
}

# 1. Verificar Java 17
echo -e "\n${YELLOW}[1/5] Verificando Java...${NC}"
if check_command java; then
    JAVA_VERSION=$(java -version 2>&1 | head -n 1 | awk -F '"' '{print $2}')
    echo "Java está instalado: $JAVA_VERSION"
    if [[ "$JAVA_VERSION" == "17"* ]]; then
        echo -e "${GREEN}✔ Java 17 detectado correctamente.${NC}"
    else
        echo -e "${RED}✘ Se detectó Java $JAVA_VERSION, pero se requiere Java 17.${NC}"
        echo "Por favor, instale Java 17 y configure JAVA_HOME antes de continuar."
        exit 1
    fi
else
    echo -e "${RED}✘ Java no está instalado.${NC}"
    echo "Por favor, instale Java 17 antes de continuar."
    exit 1
fi

# Detectar gestor de paquetes
PACKAGE_MANAGER=""
if check_command apt; then
    PACKAGE_MANAGER="apt"
elif check_command pacman; then
    PACKAGE_MANAGER="pacman"
else
    echo -e "${YELLOW}Gestor de paquetes no soportado automáticamente (se requiere apt o pacman). Saltando instalaciones automáticas.${NC}"
fi

# 2. Verificar Maven
echo -e "\n${YELLOW}[2/5] Verificando Maven...${NC}"
if check_command mvn; then
    mvn -version | head -n 1
    echo -e "${GREEN}✔ Maven está instalado.${NC}"
else
    echo -e "${YELLOW}Maven no encontrado. Intentando instalar...${NC}"
    if [[ "$PACKAGE_MANAGER" == "apt" ]]; then
        sudo apt update && sudo apt install -y maven
    elif [[ "$PACKAGE_MANAGER" == "pacman" ]]; then
        sudo pacman -S --noconfirm maven
    else
        echo -e "${RED}No se pudo instalar Maven automáticamente. Por favor instálelo manualmente.${NC}"
        exit 1
    fi
    
    if check_command mvn; then
        echo -e "${GREEN}✔ Maven instalado correctamente.${NC}"
    else
        echo -e "${RED}Error al instalar Maven.${NC}"
        exit 1
    fi
fi

# 3. Verificar Docker (para MySQL)
echo -e "\n${YELLOW}[3/5] Verificando Docker...${NC}"
DOCKER_COMPOSE_INSTALLED=false

if check_command docker; then
    if check_command docker-compose; then
        DOCKER_COMPOSE_INSTALLED=true
    elif docker compose version > /dev/null 2>&1; then
        DOCKER_COMPOSE_INSTALLED=true
    fi

    if [ "$DOCKER_COMPOSE_INSTALLED" = true ]; then
        echo -e "${GREEN}✔ Docker y Docker Compose están instalados.${NC}"
        
        if docker info > /dev/null 2>&1; then
            echo -e "${GREEN}✔ Docker daemon está corriendo.${NC}"
        else
            echo -e "${YELLOW}⚠ Docker daemon no parece estar corriendo o requiere sudo.${NC}"
            echo "Intente iniciar el servicio con: sudo systemctl start docker"
        fi
    else
        echo -e "${RED}✘ Docker está instalado pero no se detectó Docker Compose.${NC}"
        echo "Por favor instale Docker Compose."
    fi
else
    echo -e "${RED}✘ Docker no encontrado.${NC}"
    echo "Se requiere Docker y Docker Compose para la base de datos MySQL."
    echo "Por favor instale Docker y Docker Compose manualmente."
fi

# 4. Configuración de application.properties
echo -e "\n${YELLOW}[4/5] Configurando application.properties...${NC}"
RESOURCE_DIR="src/main/resources"
PROP_FILE="$RESOURCE_DIR/application.properties"
EXAMPLE_FILE="$RESOURCE_DIR/.app-properties-example"

if [ -f "$PROP_FILE" ]; then
    echo -e "${GREEN}✔ application.properties ya existe.${NC}"
else
    if [ -f "$EXAMPLE_FILE" ]; then
        echo "Copiando configuración de ejemplo a application.properties..."
        cp "$EXAMPLE_FILE" "$PROP_FILE"
        echo -e "${GREEN}✔ Archivo creado exitosamente.${NC}"
        echo -e "${YELLOW}IMPORTANTE: Revise $PROP_FILE y actualice las credenciales de base de datos y rutas.${NC}"
    else
        echo -e "${RED}✘ No se encontró $EXAMPLE_FILE. No se pudo crear la configuración.${NC}"
    fi
fi

# 5. Configuración de Directorios
echo -e "\n${YELLOW}[5/5] Verificando directorios...${NC}"
UPLOADS_DIR="uploads"

if [ -d "$UPLOADS_DIR" ]; then
    echo -e "${GREEN}✔ Directorio 'uploads' ya existe.${NC}"
else
    echo "Creando directorio 'uploads'..."
    mkdir -p "$UPLOADS_DIR"
    echo -e "${GREEN}✔ Directorio 'uploads' creado.${NC}"
fi

echo -e "\n${GREEN}=== Configuración finalizada ===${NC}"
echo "Recuerde:"
echo "1. Revisar src/main/resources/application.properties"
echo "2. Asegurarse de que el contenedor de base de datos esté corriendo si usa Docker"
echo "3. Ejecutar 'mvn spring-boot:run' para iniciar la aplicación"

# 📋 Manual Técnico - Food House Sistema de Restaurante

## 🏗️ Arquitectura del Sistema

### Tecnologías Utilizadas
- **Lenguaje**: Java 8+
- **Framework GUI**: Java Swing (AWT)
- **Base de Datos**: SQLite
- **IDE Recomendado**: BlueJ
- **Driver JDBC**: SQLite JDBC Driver

### Patrón Arquitectónico
El sistema sigue un patrón **MVC (Modelo-Vista-Controlador)** con capas de acceso a datos (DAO):

```
┌─────────────────┐
│   VISTA (GUI)   │ ← Ventanas Swing
├─────────────────┤
│ CONTROLADOR     │ ← SistemaRestaurante
├─────────────────┤
│   MODELO        │ ← Entidades (Usuario, Producto, Mesa, Orden)
├─────────────────┤
│      DAO        │ ← Capa de acceso a datos
├─────────────────┤
│   BASE DATOS    │ ← SQLite
└─────────────────┘
```
## 🗂️ Estructura del Proyecto

### Archivos Principales

#### Clases de Entidad (Modelo)
- **`Usuario.java`** - Gestión de usuarios y roles
- **`Producto.java`** - Catálogo de productos del menú
- **`Mesa.java`** - Gestión de mesas del restaurante
- **`Orden.java`** - Pedidos y comandas
- **`ItemOrden.java`** - Detalles de cada orden

#### Clases DAO (Data Access Object)
- **`UsuarioDAO.java`** - CRUD de usuarios en BD
- **`ProductoDAO.java`** - CRUD de productos
- **`MesaDAO.java`** - Gestión de mesas
- **`OrdenDAO.java`** - Gestión de órdenes

#### Clases de Control
- **`SistemaRestaurante.java`** - Lógica principal del sistema
- **`Database.java`** - Gestión en memoria de datos
- **`DatabaseConnection.java`** - Conexión a SQLite

#### Interfaces Gráficas (Vista)
- **`VentanaLogin.java`** - Autenticación de usuarios
- **`VentanaAdmin.java`** - Panel administrativo
- **`VentanaMesero.java`** - Interfaz para meseros
- **`VentanaCocina.java`** - Panel de cocina
- **`VentanaGestionMesas.java`** - Gestión de mesas
- **`VentanaAdminOrdenes.java`** - Administración de órdenes

#### Punto de Entrada
- **`RestauranteMain.java`** - Clase principal con método main()
---

## 🛠️ Configuración y Despliegue

### Requisitos del Sistema
- **Java Runtime Environment (JRE)** 8 o superior
- **SQLite JDBC Driver** en el classpath
- **Memoria RAM**: Mínimo 512MB
- **Espacio en disco**: 50MB (incluida BD)

### Configuración en BlueJ
1. **Agregar Driver SQLite**:
   - Tools → Preferences → Libraries
   - Agregar archivo `sqlite-jdbc.jar`

2. **Configurar Proyecto**:
   - Abrir carpeta del proyecto
   - Verificar todas las clases compiladas

### Archivos de Configuración
- **Base de Datos**: `restaurante.db` (se crea automáticamente)
- **Configuración**: Parámetros hardcodeados en clases
- **Logs**: Salida estándar a consola

---

## 📊 Monitoreo y Logs

### Mensajes del Sistema
```java
// Niveles de logging implementados
System.out.println("✅ Conectado a SQLite");
System.out.println("⚠️ Tabla ordenes no existe, creándola...");
System.out.println("❌ Error de conexión: " + e.getMessage());
```

### Métricas Importantes
- **Tiempo de respuesta** de consultas SQL
- **Uso de memoria** de la aplicación
- **Concurrent users** conectados
- **Órdenes por minuto** procesadas

---

## 🔄 Ciclo de Vida del Software

### Fases de Desarrollo
1. **Análisis**: Requisitos del restaurante
2. **Diseño**: Arquitectura MVC + DAO
3. **Implementación**: Java + Swing + SQLite
4. **Pruebas**: Unitarias y de integración
5. **Despliegue**: Standalone con JRE

### Mantenimiento Preventivo
- **Backup diario** de `restaurante.db`
- **Limpiar logs** periódicamente
- **Actualizar Java** a últimas versiones
- **Monitorear performance** del sistema
---
*© 2024 Food House Sistema de Restaurante - Manual Técnico*
*Versión: 1.0 | Última Actualización: 2026*

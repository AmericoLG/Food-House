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

---

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

## 🗄️ Esquema de Base de Datos

### Tablas Principales

#### `usuarios`
```sql
CREATE TABLE usuarios (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    usuario TEXT NOT NULL UNIQUE,
    password TEXT NOT NULL,
    nombre TEXT DEFAULT '',
    rol TEXT NOT NULL,
    activo INTEGER DEFAULT 1
);
```

#### `productos`
```sql
CREATE TABLE productos (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre TEXT NOT NULL,
    descripcion TEXT,
    precio_venta REAL NOT NULL,
    disponible INTEGER DEFAULT 1
);
```

#### `mesas`
```sql
CREATE TABLE mesas (
    numero INTEGER PRIMARY KEY,
    capacidad INTEGER NOT NULL,
    estado TEXT DEFAULT 'Libre'
);
```

#### `ordenes`
```sql
CREATE TABLE ordenes (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    mesa_id INTEGER NOT NULL,
    mesero TEXT NOT NULL,
    estado TEXT DEFAULT 'Pendiente',
    fecha DATETIME DEFAULT CURRENT_TIMESTAMP
);
```

#### `detalle_orden`
```sql
CREATE TABLE detalle_orden (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    id_orden INTEGER NOT NULL,
    id_producto INTEGER NOT NULL,
    cantidad INTEGER NOT NULL,
    notas TEXT,
    estado TEXT DEFAULT 'Pendiente',
    FOREIGN KEY (id_orden) REFERENCES ordenes(id),
    FOREIGN KEY (id_producto) REFERENCES productos(id)
);
```

---

## 🔧 Flujo de Ejecución

### 1. Inicialización del Sistema
```java
// RestauranteMain.java
public static void main(String[] args) {
    // 1. Configurar Look & Feel nativo
    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    
    // 2. Inicializar base de datos SQLite
    if (DatabaseConnection.testConnection()) {
        System.out.println("✅ Base de datos lista.");
    }
    
    // 3. Iniciar interfaz de login
    SwingUtilities.invokeLater(() -> new VentanaLogin().setVisible(true));
}
```

### 2. Conexión a Base de Datos
```java
// DatabaseConnection.java
public static Connection getConnection() {
    if (connection == null || connection.isClosed()) {
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:restaurante.db");
        inicializarBaseDeDatos(); // Crea tablas si no existen
    }
    return connection;
}
```

### 3. Flujo de Autenticación
```java
// SistemaRestaurante.java
public boolean login(String usuario, String password) {
    Usuario u = usuarioDAO.login(usuario, password);
    if (u != null) {
        usuarioActual = u;
        return true;
    }
    return false;
}
```

---

## 🎨 Diseño de Interfaces

### Paleta de Colores
- **Fondo Principal**: `new Color(61, 41, 20)` (Café oscuro)
- **Fondo Secundario**: `new Color(92, 61, 30)` (Café medio)
- **Acentos**: `new Color(212, 175, 55)` (Dorado)
- **Texto**: `new Color(245, 245, 220)` (Beige)
- **Botones**: `new Color(139, 69, 19)` (Café silla)

### Componentes GUI Principales

#### VentanaLogin
- Campos: Usuario, Contraseña, Rol (ComboBox)
- Autenticación contra base de datos
- Redirección según rol de usuario

#### VentanaAdmin
- **Pestañas**: Productos, Empleados, Reportes, Mesas
- **CRUD completo** para todas las entidades
- **Reportes** en tiempo real

#### VentanaMesero
- **Mapa de mesas** visual
- **Sistema de pedidos** con carrito
- **Gestión de cuentas** y pagos

#### VentanaCocina
- **Tabla de comandas** en tiempo real
- **Actualización automática** cada 5 segundos
- **Estados**: Pendiente → Listo → Entregado

---

## ⚡ Procesos de Negocio

### 1. Proceso de Pedido
```
Cliente → Mesero → Sistema → Cocina → Mesero → Cliente
    ↓        ↓         ↓        ↓        ↓        ↓
  Llega   Toma     Envía    Prepara   Sirve   Paga
pedido  pedido   orden    comida   comida  cuenta
```

### 2. Gestión de Estados
```java
// Estados de Mesa
public enum EstadoMesa {
    LIBRE, OCUPADA, RESERVADA, MANTENIMIENTO
}

// Estados de Orden
public enum EstadoOrden {
    PENDIENTE, EN_PREPARACION, LISTO, ENTREGADO, CANCELADO
}

// Estados de ItemOrden
public enum EstadoItem {
    PENDIENTE, PREPARANDO, LISTO, ENTREGADO
}
```

### 3. Flujo de Datos
```
Interfaz Swing → Controlador → DAO → SQLite
     ↑              ↓           ↓      ↓
  Usuario     SistemaRestaurante  JDBC  Archivo.db
```

---

## 🔌 Integración de Componentes

### 1. Sistema de Eventos
```java
// Ejemplo: ActionListener para botón
btnAgregar.addActionListener(new ActionListener() {
    public void actionPerformed(ActionEvent e) {
        // Lógica de negocio
        sistema.agregarProducto(producto);
        // Actualizar interfaz
        actualizarTabla();
    }
});
```

### 2. Actualización en Tiempo Real
```java
// Timer para cocina (actualización cada 5 segundos)
Timer timer = new Timer(5000, e -> {
    actualizarTablaOrdenes();
});
timer.start();
```

### 3. Manejo de Excepciones
```java
try {
    Connection conn = DatabaseConnection.getConnection();
    // Operaciones BD
} catch (SQLException e) {
    JOptionPane.showMessageDialog(null, 
        "Error de base de datos: " + e.getMessage(),
        "Error", JOptionPane.ERROR_MESSAGE);
}
```

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

## 🔍 Depuración y Mantenimiento

### Puntos de Control Clave
```java
// Verificación de conexión
if (DatabaseConnection.testConnection()) {
    System.out.println("✅ BD conectada");
}

// Validación de usuario
if (sistema.login(user, pass)) {
    System.out.println("✅ Login exitoso");
}

// Estados de mesa
System.out.println("Mesa " + mesa.getNumero() + ": " + mesa.getEstado());
```

### Problemas Comunes y Soluciones

#### Error: "Driver SQLite no encontrado"
```java
// Solución: Verificar classpath
Class.forName("org.sqlite.JDBC");
```

#### Error: "Base de datos no encontrada"
```java
// Solución: Verificar ruta del archivo
String url = "jdbc:sqlite:restaurante.db";
File dbFile = new File("restaurante.db");
if (!dbFile.exists()) {
    // Crear base de datos
    DatabaseConnection.getConnection();
}
```

#### Problemas de Concurrencia
```java
// Solución: Usar synchronized para operaciones críticas
public synchronized boolean agregarOrden(Orden orden) {
    // Operación atómica
}
```

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

## 🚀 Mejoras Futuras

### Arquitectura
- **Migrar a Spring Boot** para mejor escalabilidad
- **Implementar REST API** para clientes móviles
- **Base de datos cliente-servidor** (MySQL/PostgreSQL)

### Funcionalidades
- **Sistema de reservas** online
- **Integración pasarelas de pago**
- **Módulo de inventario** avanzado
- **Reportes analíticos** con gráficos

### Técnico
- **Testing automatizado** con JUnit
- **Logging estructurado** con Log4j
- **Configuración externa** (properties)
- **Internacionalización** (i18n)

---

## 📞 Soporte Técnico

### Contacto de Desarrollo
- **Arquitectura Principal**: Patrón MVC con DAO
- **Base de Datos**: SQLite embebido
- **Interfaz**: Java Swing nativo

### Recursos Técnicos
- **Código Fuente**: Comentarios detallados en cada clase
- **Esquema BD**: Definido en `DatabaseConnection.java`
- **Logs**: Consola estándar y diálogos de error

---

*© 2024 Food House Sistema de Restaurante - Manual Técnico*
*Versión: 1.0 | Última Actualización: 2024*

# 📖 Manual de Usuario - Food House Sistema de Restaurante

## 🚀 Instalación y Configuración

### Requisitos Previos
- Java 8 o superior instalado
- BlueJ (recomendado para desarrollo)
- Driver SQLite JDBC

### Pasos de Instalación
1. **Descargar el proyecto**: Extraer el archivo ZIP en una carpeta local
2. **Abrir en BlueJ**: 
   - Iniciar BlueJ
   - Ir a `Proyecto -> Abrir Proyecto`
   - Seleccionar la carpeta del proyecto
3. **Configurar librería SQLite**:
   - En BlueJ: `Tools -> Preferences -> Libraries`
   - Agregar el driver SQLite JDBC
4. **Ejecutar**: 
   - Clic derecho en `RestauranteMain`
   - Seleccionar `void main(String[] args)`

### Primer Inicio
- Al ejecutar por primera vez, se creará automáticamente el archivo `restaurante.db`
- Si aparece un error SQL, borre el archivo `restaurante.db` y reinicie

---

## 🔐 Acceso al Sistema

### Credenciales por Defecto
| Rol | Usuario | Contraseña | Permisos |
|:---|:---|:---|:---|
| **Administrador** | `admin` | `admin123` | Gestión completa del sistema |
| **Mesero** | `mesero` | `mesero123` | Toma de pedidos y gestión de mesas |
| **Cocinero** | `cocina` | `cocina123` | Gestión de comandas de cocina |

### Inicio de Sesión
1. Ejecutar el programa
2. Ingresar usuario y contraseña
3. Seleccionar el rol correspondiente
4. Hacer clic en "Iniciar Sesión"

---

## 🎛️ Panel de Administración

### Acceso al Panel
- Iniciar sesión como `admin` / `admin123`

### Funcionalidades Principales

#### 📦 Gestión de Productos
**Ubicación**: Pestaña "Gestión de Productos"

**Operaciones CRUD**:
- **Crear Producto**:
  - Hacer clic en "Agregar Producto"
  - Completar: Nombre, Descripción, Precio, Categoría, Stock
  - Hacer clic en "Guardar"

- **Editar Producto**:
  - Seleccionar producto de la tabla
  - Hacer clic en "Editar"
  - Modificar los campos necesarios
  - Hacer clic en "Actualizar"

- **Eliminar Producto**:
  - Seleccionar producto
  - Hacer clic en "Eliminar"
  - Confirmar en el diálogo

- **Buscar Productos**:
  - Usar el campo de búsqueda por nombre
  - Los resultados se actualizan automáticamente

#### 👥 Gestión de Empleados
**Ubicación**: Pestaña "Gestión de Empleados"

**Operaciones**:
- **Agregar Empleado**: Completar formulario con datos personales
- **Editar Empleado**: Modificar información existente
- **Eliminar Empleado**: Remover del sistema (con confirmación)
- **Buscar**: Filtrar por nombre o puesto

#### 📊 Reportes y Estadísticas
**Ubicación**: Pestaña "Reportes"

**Reportes Disponibles**:
- **Ventas del Día**: Resumen de transacciones diarias
- **Productos Más Vendidos**: Ranking de popularidad
- **Inventario Actual**: Estado actual del stock
- **Reportes Personalizados**: Por rango de fechas

#### 🖥️ Gestión de Mesas
**Ubicación**: Pestaña "Gestión de Mesas"

**Operaciones**:
- **Ver Estado**: Ocupadas/Libres
- **Asignar Mesa**: Cambiar estado de mesa
- **Capacidad**: Configurar número de comensales

---

## 🍽️ Panel de Mesero

### Acceso al Panel
- Iniciar sesión como `mesero` / `mesero123`

### Funcionalidades Principales

#### 📋 Toma de Pedidos
1. **Seleccionar Mesa**:
   - Ver mapa de mesas disponibles (verde=libre, rojo=ocupada)
   - Hacer clic en mesa deseada

2. **Agregar Productos**:
   - Buscar productos por categoría
   - Añadir al carrito con cantidad
   - Ver subtotal en tiempo real

3. **Confirmar Pedido**:
   - Revisar orden final
   - Hacer clic en "Enviar a Cocina"
   - El pedido aparecerá automáticamente en cocina

#### 💳 Gestión de Cuentas
**Operaciones**:
- **Ver Cuentas Activas**: Lista de mesas con pedidos
- **Cerrar Cuenta**: 
  - Seleccionar mesa
  - Ver desglose de consumo
  - Procesar pago
  - Imprimir comprobante (opcional)

#### 🔄 Estado de Mesas
- **Visualización en Tiempo Real**: Actualización automática
- **Cambio de Estado**: Libre → Ocupada → Disponible
- **Notificaciones**: Cuando cocina marca pedido como "Listo"

---

## 👨‍🍳 Panel de Cocina

### Acceso al Panel
- Iniciar sesión como `cocina` / `cocina123`

### Funcionalidades Principales

#### 📋 Gestión de Comandas
**Vista Principal**:
- **Tabla de Órdenes**: Muestra todos los pedidos activos
- **Columnas**: N° Orden, Mesa, Producto, Cantidad, Estado, Acciones

#### 🔄 Estados de Pedido
**Estados Disponibles**:
- **⏳ Pendiente**: Pedido recién recibido (color amarillo)
- **✅ Listo**: Pedido terminado (color verde)
- **🚚 Entregado**: Pedido entregado al mesero

#### ⚡ Operaciones en Tiempo Real
**Actualización Automática**:
- La tabla se actualiza cada 5 segundos
- Nuevos pedidos aparecen automáticamente
- Cambios de estado se reflejan inmediatamente

**Cambiar Estado**:
1. Seleccionar pedido de la tabla
2. Hacer clic en botón de estado correspondiente
3. Confirmar cambio
4. El mesero recibe notificación automática

#### 🔔 Notificaciones
- **Nuevos Pedidos**: Indicador visual y sonido (opcional)
- **Pedidos Listos**: Resaltado en verde
- **Tiempo de Espera**: Mostrar tiempo transcurrido por pedido

---

## 🎯 Flujo de Trabajo Completo

### 1. Apertura del Restaurante
1. **Administrador**: Inicia sesión y verifica inventario
2. **Meseros**: Inician sesión y revisan estado de mesas
3. **Cocina**: Inicia sesión y prepara estación de trabajo

### 2. Atención al Cliente
1. **Mesero** recibe clientes y los asigna a mesa
2. **Mesero** toma pedido usando el sistema
3. **Pedido** se envía automáticamente a cocina
4. **Cocina** recibe notificación y prepara alimentos
5. **Cocina** marca pedido como "Listo"
6. **Mesero** sirve alimentos a los clientes

### 3. Cierre de Cuenta
1. **Cliente** solicita la cuenta
2. **Mesero** procesa pago desde el sistema
3. **Mesa** se libera automáticamente
4. **Inventario** se actualiza automáticamente

### 4. Cierre del Día
1. **Administrador** genera reportes de ventas
2. **Administrador** revisa inventario restante
3. **Administrador** cierra sesión del sistema

---

## ⚙️ Configuración y Mantenimiento

### Base de Datos
- **Ubicación**: `restaurante.db` en carpeta del proyecto
- **Backup**: Recomendado copiar archivo `.db` periódicamente
- **Reinicio**: Borrar archivo para crear base limpia

### Personalización
- **Colores**: Modificar en clases de ventanas (código RGB)
- **Tipografía**: Configurar en UIManager
- **Tamaños**: Ajustar en métodos `setSize()`

### Solución de Problemas

#### ❌ Problemas Comunes
**Error de conexión SQLite**:
- ✅ Verificar driver JDBC instalado
- ✅ Reiniciar programa después de borrar `.db`

**Ventana no responde**:
- ✅ Cerrar y reiniciar aplicación
- ✅ Verificar memoria RAM disponible

**Pedidos no aparecen**:
- ✅ Verificar conexión a red (si es multiusuario)
- ✅ Revisar que ambos usuarios estén activos

**Botones sin color**:
- ✅ Es problema conocido del Look & Feel
- ✅ Forzar colores en código (ya implementado)

#### 🔧 Mantenimiento Preventivo
- **Diario**: Verificar respaldos de base de datos
- **Semanal**: Limpiar archivos temporales
- **Mensual**: Revisar log de errores del sistema

---

## 📞 Soporte Técnico

### Contacto
- **Desarrollador**: [Información de contacto]
- **Versión**: 1.0
- **Última Actualización**: [Fecha]

### Recursos Adicionales
- **Documentación Técnica**: Ver comentarios en código fuente
- **Base de Datos**: Esquema disponible en `Database.java`
- **Logs**: Consola de BlueJ para depuración

---

## 🎓 Tips y Buenas Prácticas

### Para Administradores
- 🔄 Realizar backups diarios de la base de datos
- 📊 Revisar reportes de ventas cada cierre
- 📦 Mantener inventario actualizado

### Para Meseros
- ⏰ Confirmar pedidos antes de enviar a cocina
- 📋 Verificar estado de mesa antes de asignar
- 💳 Procesar pagos inmediatamente después del servicio

### Para Cocineros
- 👀 Mantener vigilancia constante de nuevas comandas
- ✅ Marcar pedidos como listos inmediatamente
- 🔄 Comunicar demoras a meseros si es necesario

### Atajos de Teclado
- **F5**: Actualizar tabla actual
- **Esc**: Cerrar diálogo actual
- **Enter**: Confirmar acción seleccionada

---

## 🚀 Próximas Actualizaciones

### Funcionalidades en Desarrollo
- 📱 Aplicación móvil para meseros
- 🌐 Soporte multi-sucursal
- 💳 Integración con pasarelas de pago
- 📊 Dashboard en tiempo real
- 📧 Notificaciones por email

### Mejoras Planeadas
- 🎨 Interfaz mejorada con Material Design
- 🔍 Búsqueda avanzada de productos
- 📈 Análisis predictivo de ventas
- 🌍 Soporte multi-idioma

---

*© 2024 Food House Sistema de Restaurante. Todos los derechos reservados.*

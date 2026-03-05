# 🍽 Food House - Sistema de Restaurante

Sistema completo de gestión (Administración, Mesero y Cocina) desarrollado en Java con persistencia en SQLite.

## 🚀 Inicio Rápido en BlueJ

1. **Abrir Proyecto**: En BlueJ, ve a `Proyecto -> Abrir Proyecto` y selecciona la carpeta del repositorio.
2. **Configurar Librería**: Asegúrate de tener el driver de SQLite en `Tools -> Preferences -> Libraries`.
3. **Ejecutar**: Clic derecho en `RestauranteMain` -> `void main(String[] args)`.

### 🔐 Acceso al Sistema
| Rol | Usuario | Contraseña |
| :--- | :--- | :--- |
| **Administrador** | `admin` | `admin123` |
| **Mesero** | `mesero` | `mesero123` |
| **Cocinero** | `cocina` | `cocina123` |

---

## 🛠 Solución de Errores Comunes (FAQ)

* **¿Ves una ventana de error SQL al iniciar?** Cierra el programa y borra el archivo `restaurante.db` en la carpeta del proyecto. Al reiniciar, el sistema creará una base de datos limpia y funcional.
    
* **¿No compila?** Verifica que no tengas clases en rojo. Si las hay, asegúrate de haber importado todas las clases del repositorio.

---

## 🎯 Funcionalidades por Rol
* **Admin**: Control de inventario (CRUD productos), gestión de empleados y reportes.
* **Mesero**: Toma de pedidos, asignación de mesas y cierre de cuentas.
* **Cocina**: Gestión de comandas en tiempo real y cambio de estados (Pendiente/Listo).

## 🗄️ Detalles Técnicos
* **Base de Datos**: SQLite (archivo local `restaurante.db`).
* **Interfaz**: Java Swing con Look & Feel del sistema.
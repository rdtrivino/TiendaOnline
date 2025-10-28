# 🛍️ Bella Store — Tienda Online

Proyecto académico desarrollado en **Android Studio con Kotlin**, como parte de la asignatura **Diseño, modelación y desarrollo de aplicaciones para plataformas nativas**.  
El objetivo del trabajo es **diseñar, modelar y programar una aplicación móvil nativa** con almacenamiento local, CRUD de productos y navegación entre pantallas, aplicando buenas prácticas de usabilidad.

---

## 📱 Descripción del Proyecto

**Bella Store** es una aplicación móvil Android que simula una **tienda virtual completa**.  
Incluye inicio de sesión con roles, catálogo de productos administrable, carrito de compras, módulo de configuración y almacenamiento de imágenes en la memoria local.

### 🧩 Funcionalidades principales:
- 👤 **Login y registro de usuarios** (con roles *admin* y *cliente*).
- 🛒 **Gestión de productos (CRUD completo)** con imágenes, precios, stock y calificación.
- 📦 **Carrito de compras dinámico**, con cálculo automático del total.
- 🌙 **Modo oscuro**, notificaciones y política de privacidad en ajustes.
- 💅 **Servicios de belleza** con pantalla dedicada (*BeautyActivity*).
- 🎨 **Interfaz moderna y adaptable**, inspirada en Material Design.

---

## 🧱 Estructura de la aplicación

### 📂 Paquete principal: `com.rubentrivino.tiendaonline`

| Carpeta / Archivo | Descripción |
|--------------------|-------------|
| `data/DBHelper.kt` | Controlador de base de datos SQLite (creación de tablas y schema). |
| `data/ProductDAO.kt` | Lógica CRUD para productos (insertar, actualizar, eliminar, listar). |
| `data/CartDAO.kt` | Manejo del carrito de compras (insert, update, delete, total). |
| `data/Product.kt` / `CartItem.kt` / `CartRow.kt` | Modelos de datos (entidades). |
| `LoginActivity.kt` | Pantalla de inicio de sesión (manejo de roles). |
| `ProfileActivity.kt` | Perfil del usuario con menú visual. |
| `ShopActivity.kt` | Catálogo principal con **RecyclerView** y CRUD. |
| `ProductFormActivity.kt` | Formulario de creación/edición de productos (almacenamiento de imágenes). |
| `ProductDetailActivity.kt` | Detalle del producto con favorito, calificación y botón de compra. |
| `OrdersActivity.kt` | Carrito de compras, listado con cantidad, subtotal y total. |
| `SettingsActivity.kt` | Configuraciones generales (modo oscuro, notificaciones). |
| `BeautyActivity.kt` | Módulo de servicios de belleza (reserva simulada). |

---

## 🧮 Base de datos (SQLite)

La app implementa **almacenamiento local** mediante `SQLiteOpenHelper` en `DBHelper.kt`.  

**Esquema principal:**

| Tabla | Campos |
|--------|---------|
| **products** | id, name, description, price, stock, image, favorite, rating |
| **cart_items** | id, product_id, quantity |
| **users (SharedPreferences)** | email, password, isAdmin |

Permite operaciones CRUD y sincronización entre productos y carrito.

---

## 🔄 Navegación entre pantallas

1. `LoginActivity` → `ProfileActivity`  
2. `ProfileActivity` → `ShopActivity` / `SettingsActivity` / `BeautyActivity`  
3. `ShopActivity` → `ProductDetailActivity` / `ProductFormActivity` / `OrdersActivity`  
4. `OrdersActivity` → visualización y actualización del carrito  

La navegación se realiza mediante `Intent` y `startActivity()`.

---

## 🧠 Objetivos Académicos

- Implementar **RecyclerView** para la visualización dinámica de productos.  
- Aplicar **DAO pattern** y **SQLite** para persistencia de datos.  
- Incorporar **almacenamiento de imágenes locales** (SAF API).  
- Desarrollar **CRUD completo** con validaciones.  
- Integrar **configuración de usuario y modo oscuro**.  
- Diseñar interfaces visuales modernas, accesibles y funcionales.

---

## ⚙️ Tecnologías y Herramientas

| Componente | Versión / Descripción |
|-------------|------------------------|
| **Lenguaje** | Kotlin |
| **IDE** | Android Studio (Iguana o posterior) |
| **SDK Mínimo** | API 24 (Android 7.0 Nougat) |
| **Dependencias principales** | androidx.appcompat, material, constraintlayout, preference-ktx, recyclerview |
| **Base de datos** | SQLite (local) |
| **Diseño visual** | XML + Material Components |
| **Gradle JVM** | Java 17 |

---

## 🎨 Diseño de interfaz

- **Gradientes personalizados** (`bg_gradient.xml`)  
- **Botones redondeados** (`bg_primary_button.xml`)  
- **CardViews** con sombras suaves  
- **Paleta de colores azul, blanco y rosa (#E91E63)**  
- **Iconografía y emojis** para secciones visualmente amigables  

---

## 📦 Instalación y Ejecución

### 🔧 Requisitos Previos
- Android Studio instalado (versión Iguana o posterior).  
- Dispositivo físico o emulador con API ≥ 24.  

### 🚀 Pasos de instalación
```bash
git clone https://github.com/rdtrivino/BellaStore.git

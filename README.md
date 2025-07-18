# 📚 Registro de Estudiantes

Una aplicación **CRUD** (Crear, Leer, Actualizar, Eliminar) diseñada para gestionar el registro de estudiantes en una institución educativa, construida con una interfaz gráfica de usuario (GUI) utilizando **Swing**.

---

## 🚀 Características Principales

- **Registro de Nuevos Estudiantes**  
  Permite añadir nuevos estudiantes con sus datos personales y académicos a través de un formulario intuitivo.

- **Visualización de Estudiantes**  
  Muestra una lista completa y editable de todos los estudiantes registrados en una tabla interactiva.

- **Actualización de Datos**  
  Facilita la modificación de la información existente de cualquier estudiante directamente desde la interfaz gráfica.

- **Eliminación de Registros**  
  Permite eliminar permanentemente los registros de estudiantes seleccionados.

---

## 🛠️ Tecnologías Utilizadas

- **Java**  
  Lenguaje de programación principal para la lógica de la aplicación.

- **Swing**  
  Toolkit de Java para la creación de la interfaz gráfica de usuario (GUI).

- **MySQL**  
  Sistema de gestión de bases de datos relacionales utilizado para almacenar la información de los estudiantes.

- **JDBC (Java Database Connectivity)**  
  API de Java para la conexión y manipulación de la base de datos MySQL.

---

## ⚙️ Configuración y Ejecución

### 1. Configuración de la Base de Datos MySQL

- Asegúrate de tener un servidor MySQL instalado y funcionando.
- Importa el archivo SQL desde la carpeta `sql` del proyecto.
- Usa tu cliente de MySQL preferido (MySQL Workbench, PhpMyAdmin, terminal, etc.).
- Verifica que las credenciales de conexión en tu código Java (usuario, contraseña, URL) coincidan con tu configuración.

### 2. Compilación y Ejecución de la Aplicación Java

- Abre el proyecto en tu IDE favorito (IntelliJ IDEA, Eclipse o NetBeans).
- Asegúrate de incluir el conector JDBC de MySQL (.jar) en tu proyecto.
- Si no lo tienes, descárgalo desde el sitio oficial de MySQL.
- Compila y ejecuta la clase principal del proyecto.
- La interfaz gráfica de Swing debería aparecer al iniciar la aplicación.

---

## 🎓 Propósito Académico

Esta aplicación fue desarrollada como parte de una actividad académica universitaria. Su propósito es demostrar la implementación práctica de una aplicación **CRUD** utilizando:

- **Java** para la lógica de negocio  
- **Swing** para la interfaz gráfica de usuario  
- **MySQL** para la persistencia de datos  

Sirve como ejemplo para comprender y aplicar los principios fundamentales de la interacción entre una aplicación con GUI y una base de datos relacional.

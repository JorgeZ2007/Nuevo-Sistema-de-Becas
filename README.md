# Sistema de Gestión de Becas (SIA)

> **Proyecto:** SIA  
> **Entorno:** Java 8 (JDK 1.8) / NetBeans IDE 8.2  

---

## Descripción del Proyecto

El **Sistema de Gestión de Becas** es una solución de software orientada a objetos en Java diseñada para administrar becas académicas y socioeconómicas, automatizar la evaluación de postulantes y optimizar la asignación presupuestaria.

El sistema cuenta con un **doble modo de ejecución** (Por consola y por ventana) y permite la persistencia de datos mediante archivos estructurados CSV ().

---

## Como ejecutar el codigo

Para ejecutar el programa, se debe extraer el archivo .zip, abrir la carpeta con el proyecto en NetBeans y oprimir el boton Run :)

---

## Estructura del Proyecto y Paquetes

El código fuente está organizado en el paquete principal `sistemadebecas` bajo el patrón de diseño de capas:

```text
src/
└── sistema/
│   ├── SistemaDeBecas.java # Clase en donde se ejecuta el MAIN
└── sistemadebecas/
    ├── modelo/                 # Clases de Dominio y Jerarquía de Herencia
    │   ├── Persona.java            # Clase base con atributos de identidad
    │   ├── Beneficiario.java       # Subclase de Persona (Promedio, Quintil, Carrera)
    │   ├── Beca.java               # Clase Abstracta base para el catálogo
    │   ├── BecaAcademica.java      # Subclase con requisitos de excelencia académica
    │   ├── BecaSocioeconomica.java # Subclase con filtros de vulnerabilidad
    │   └── Requisito.java          # Composición para validación de postulaciones
    ├── servicio/               # Lógica de Negocio y Persistencia
    │   ├── GestorBecas.java        # Controlador (CRUD, HashMap, Listas anidadas)
    │   └── PersistenciaCSV.java    # Lectura/Escritura batch en datos_becas.csv
    ├── excepciones/            # Control de Excepciones Personalizadas
    │   ├── BecaNoEncontradaException.java
    │   └── RequisitoNoCumplidoException.java
    ├── vista/                  # Capa de Interfaz de Usuario
    │   ├── MenuConsola.java        # Menú interactivo por consola 
    │   └── VentanaPrincipal.java   # Interfaz gráfica de usuario 


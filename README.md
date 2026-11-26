# KeyCommand

Mod cliente para Minecraft **1.21.4** con **Fabric**. Permite guardar comandos y ejecutarlos con teclas asignadas.

## Uso

- La tecla predeterminada del menú es **Ñ** (en el mapa GLFW/US corresponde a `;`).
- Al primer inicio se crean estos ejemplos: **G → `/warp end`** y **H → `/warp nether`**.
- Abre el menú, escribe un comando y pulsa **Añadir**. Después pulsa **Sin tecla** y presiona la tecla que quieras asignar.
- Los binds se guardan en `config/keycommand.txt`.
- Para borrar una asignación, pulsa **X**. Pulsa Escape para cerrar el menú.

Los comandos se envían como comandos normales del jugador. El servidor debe reconocerlos y tu cuenta debe tener los permisos necesarios.

## Compilar

Requiere **Java 21** y Gradle. Ejecuta `gradle build`; el JAR se genera en `build/libs/`.
# Spring fotos

Proyecto que permite subir fotos al servidor mediante una API REST hecha con Spring Boot, que está conectado a una base
de datos MySQL.

## Funcionalidades

- [x] Listar productos
- [x] Registrar productos
- [x] Mostrar foto de los productos
- [x] Subir y actualizar fotos de los productos

## Cómo funciona?

El usuario enviará su producto mediante nuestro cliente (Página web, App móvil, Postman, etc). <br/>
Campos obligatorios para que funcione: <br/>

- nombre (nombre del producto)
- desc (descripción del producto)
- foto (foto del producto)

<p align="center">
    <img width="750" src="https://github.com/sebastian-reyes/spring-fotos/blob/master/repo/img/README_registrarProducto.png">
</p>

Después la foto se guardará en el servidor, y su nombre será registrado en la base de datos con un id único para evitar
que se repitan los nombres de la foto. <br/>

<p align="center">
    <img width="750" src="https://github.com/sebastian-reyes/spring-fotos/blob/master/repo/img/README_fotoBDD.png">
</p>

La consulta para obtener la foto se hará mediante el nombre, el servidor buscará la foto entre sus archivos y la
mostrará al usuario

<p align="center">
    <img width="750" src="https://github.com/sebastian-reyes/spring-fotos/blob/master/repo/img/README_mostrarFoto.png">
</p>

## Endpoints

El proyecto cuenta con 4 endpoints:

```bash
(GET)  http://localhost:9898/productos/{id}
(GET)  http://localhost:9898/productos/foto/{id}
(POST) http://localhost:9898/productos/registrar
(POST) http://localhost:9898/productos/foto
```

## Tecnologías usadas
- Java 8
- MySQl
- Spring Framework
- Lombok
- JPA y JDBC
- Postman (Pruebas)
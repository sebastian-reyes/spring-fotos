package com.sreyes.app.rest;

import com.sreyes.app.model.Producto;
import com.sreyes.app.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.activation.FileTypeMap;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/productos")
public class ProductoRestController {

    @Autowired
    private ProductoService service;

    @GetMapping("/{id}")
    public ResponseEntity<?> getProducto(@PathVariable Integer id) {
        Producto producto = null;
        Map<String, Object> response = new HashMap<>();
        try {
            producto = service.buscarProducto(id);
            if (producto == null) {
                response.put("mensaje", "El producto no se encuentra en la base de datos.");
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(producto, HttpStatus.OK);
        } catch (DataAccessException e) {
            response.put("message", "Error al consultar la base de datos.");
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/foto/{id}")
    public ResponseEntity<?> getFotoProducto(@PathVariable Integer id) throws IOException {
        Producto producto = null;
        String nombre_foto = null;
        Map<String, Object> response = new HashMap<>();
        try {
            producto = service.buscarProducto(id);
            if (producto == null) {
                response.put("mensaje", "El producto no se encuentra en la base de datos.");
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
            }
            nombre_foto = producto.getFoto();
            if (nombre_foto == null) {
                response.put("mensaje", "Este producto no cuenta con foto.");
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
            }
            File img = new File("src/main/resources/static/fotos/" + nombre_foto);
            return ResponseEntity.ok()
                    .contentType(MediaType.valueOf(FileTypeMap.getDefaultFileTypeMap().getContentType(img)))
                    .body(Files.readAllBytes(img.toPath()));
        } catch (DataAccessException e) {
            response.put("message", "Error al consultar la base de datos.");
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/foto")
    public ResponseEntity<?> subirFoto(@RequestParam("foto") MultipartFile foto, @RequestParam("id") Integer id) {
        Producto producto = service.buscarProducto(id);
        Map<String, Object> response = new HashMap<>();
        if (foto.isEmpty()) {
            response.put("mensaje", "Los campos no pueden estar vacíos");
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
        }
        String nombreFoto = UUID.randomUUID().toString() + "_" + foto.getOriginalFilename().replace(" ", "");
        Path rutaFoto = Paths.get("src\\main\\resources\\static\\fotos").resolve(nombreFoto).toAbsolutePath();
        try {
            Files.copy(foto.getInputStream(), rutaFoto);
        } catch (Exception e) {
            response.put("mensaje", "Error al subir la imagen");
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        /*
         * Acá borramos la foto anterior del usuario en caso que suba una
         * nueva (Actualizar foto)
         */
        String nombreFotoAnterior = producto.getFoto();
        if (nombreFotoAnterior != null && nombreFotoAnterior.length() > 0) {
            Path rutaFotoAnterior = Paths.get("src\\main\\resources\\static\\fotos").resolve(nombreFotoAnterior).toAbsolutePath();
            File archivoFotoAnterior = rutaFotoAnterior.toFile();
            if (archivoFotoAnterior.exists() && archivoFotoAnterior.canRead()) {
                archivoFotoAnterior.delete();
            }
        }
        //Se guarda la foto y retornamos el producto más un mensaje
        producto.setFoto(nombreFoto);
        service.guardarProducto(producto);
        response.put("producto", producto);
        response.put("mensaje", "Ha subido correctamente la imagen: " + nombreFoto);
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
    }

    @PostMapping("/registrar")
    public ResponseEntity<?> guardarUsuario(@RequestParam("foto") MultipartFile foto,
                                            @RequestParam String nombre,
                                            @RequestParam String desc) {
        Producto nuevoProducto = new Producto();
        Map<String, Object> response = new HashMap<>();
        try {
            if (nombre == null || desc == null || foto.isEmpty()) {
                response.put("mensaje", "Faltan parámetros obligatorios.");
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
            }
            String nombreFoto = UUID.randomUUID().toString() + "_" + foto.getOriginalFilename().replace(" ", "");
            Path rutaFoto = Paths.get("src\\main\\resources\\static\\fotos").resolve(nombreFoto).toAbsolutePath();

            nuevoProducto.setNombre(nombre);
            nuevoProducto.setDescripcion(desc);
            try {
                Files.copy(foto.getInputStream(), rutaFoto);
                nuevoProducto.setFoto(nombreFoto);
                service.guardarProducto(nuevoProducto);
                response.put("producto", nuevoProducto);
                response.put("mensaje", "Ha registrado correctamente el producto.");
            } catch (Exception e) {
                response.put("mensaje", "Error al subir la imagen");
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (DataAccessException e) {
            response.put("message", "Error al registrar en la base de datos.");
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
    }
}

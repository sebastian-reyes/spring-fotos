package com.sreyes.app.rest;

import com.sreyes.app.model.Producto;
import com.sreyes.app.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.activation.FileTypeMap;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/productos")
public class ProductoRestController {

    @Autowired
    private ProductoService service;

    @GetMapping("/foto/{id}")
    public ResponseEntity<?> getFotoProducto(@PathVariable Integer id) throws IOException {
        Producto producto = null;
        String nombre_foto = null;
        Map<String, Object> response = new HashMap<>();
        try {
            producto = service.buscarProducto(id);
            if(producto == null){
                response.put("mensaje", "El producto no se encuentra en la base de datos.");
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
            }
            nombre_foto = producto.getFoto();
            if(nombre_foto == null){
                response.put("mensaje", "Este producto no cuenta con foto.");
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
            }
            File img = new File("src/main/resources/static/fotos/"+ nombre_foto);
            return ResponseEntity.ok()
                    .contentType(MediaType.valueOf(FileTypeMap.getDefaultFileTypeMap().getContentType(img)))
                    .body(Files.readAllBytes(img.toPath()));
        }catch (DataAccessException e) {
            response.put("message", "Error al consultar la base de datos.");
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

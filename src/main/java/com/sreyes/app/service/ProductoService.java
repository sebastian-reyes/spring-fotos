package com.sreyes.app.service;

import com.sreyes.app.dao.ProductoRepository;
import com.sreyes.app.interfaceService.IProductoService;
import com.sreyes.app.model.Producto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductoService implements IProductoService {

    @Autowired
    ProductoRepository repository;

    @Override
    public Producto buscarProducto(int id) {
        return repository.findById(id).orElse(null);
    }
}

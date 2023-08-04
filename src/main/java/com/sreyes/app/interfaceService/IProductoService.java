package com.sreyes.app.interfaceService;

import com.sreyes.app.model.Producto;

public interface IProductoService {
    public Producto buscarProducto(int id);
    public Producto guardarProducto(Producto p);
}

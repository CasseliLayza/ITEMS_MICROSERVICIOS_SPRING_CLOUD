package com.backend.springcloud.msvc.items.client;

import com.backend.lib.mcsv.commons.entity.Product;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//@FeignClient(url = "localhost:8001/products", name = "microservicios")
@FeignClient(path = "/products", name = "msvc-products")
public interface ProductFeignClient {

    @GetMapping("/list")
    List<Product> listarProductos();

    @GetMapping("/find/{id}")
    Product buscarProducto(@PathVariable Long id);

    @PostMapping("/create")
    Product crearProducto(@RequestBody Product product);

    @PutMapping("/update/{id}")
    Product actualizarProducto(@RequestBody Product product, @PathVariable Long id);

    @DeleteMapping("/delete/{id}")
    void eliminarProducto(@PathVariable Long id);


}

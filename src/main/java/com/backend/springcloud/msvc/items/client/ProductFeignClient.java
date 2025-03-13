package com.backend.springcloud.msvc.items.client;

import com.backend.springcloud.msvc.items.model.Product;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

//@FeignClient(url = "localhost:8001/products", name = "microservicios")
@FeignClient( path = "/products", name = "msvc-products")
public interface ProductFeignClient {

    @GetMapping("/list")
    List<Product> listarProductos();

    @GetMapping("/find/{id}")
    Product buscarProducto(@PathVariable Long id);


}

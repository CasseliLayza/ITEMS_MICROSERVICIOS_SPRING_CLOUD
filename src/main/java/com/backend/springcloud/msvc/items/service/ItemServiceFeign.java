package com.backend.springcloud.msvc.items.service;

import com.backend.lib.mcsv.commons.entity.Product;
import com.backend.springcloud.msvc.items.client.ProductFeignClient;
import com.backend.springcloud.msvc.items.model.Item;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Service
//@Primary
public class ItemServiceFeign implements IItemService {
    private final Logger LOGGER = LoggerFactory.getLogger(ItemServiceFeign.class);
    @Autowired
    private ProductFeignClient productFeignClient;


    @Override
    public List<Item> findAll() {
        LOGGER.info(">>> Procces Feign findAll \n");

        List<Item> listItems = productFeignClient.listarProductos()
                .stream()
                .map(product -> new Item(product, new Random().nextInt(10) + 1)).collect(Collectors.toList());

        for (Item item : listItems) {
            System.out.println("item = " + item);
        }

        return listItems;
    }

    @Override
    public Optional<Item> findById(Long id) {
        LOGGER.info(">>> Procces Feign findById \n");

        try {
            Product product = productFeignClient.buscarProducto(id);
            return Optional.of(new Item(product, new Random().nextInt(10) + 1));

        } catch (FeignException e) {
            return Optional.empty();

        }
    }

    @Override
    public Product save(Product product) {
        LOGGER.info(">>> Procces Feign save \n");
        return productFeignClient.crearProducto(product);
    }

    @Override
    public Product update(Product product, Long id) {
        LOGGER.info(">>> Procces Feign update \n");
        return productFeignClient.actualizarProducto(product, id);
    }

    @Override
    public void delete(Long id) {
        LOGGER.info(">>> Procces Feign delete \n");
        try {
            productFeignClient.eliminarProducto(id);
        } catch (FeignException e) {
            LOGGER.error("Error: " + e.getMessage());
        }
    }
}

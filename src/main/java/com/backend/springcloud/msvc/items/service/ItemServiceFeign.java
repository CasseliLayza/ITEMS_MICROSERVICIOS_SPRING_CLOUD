package com.backend.springcloud.msvc.items.service;

import com.backend.springcloud.msvc.items.client.ProductFeignClient;
import com.backend.springcloud.msvc.items.model.Item;
import com.backend.springcloud.msvc.items.model.Product;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class ItemServiceFeign implements IItemService {

    @Autowired
    private ProductFeignClient productFeignClient;


    @Override
    public List<Item> findAll() {
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
        try {
            Product product = productFeignClient.buscarProducto(id);
            return Optional.of(new Item(product, new Random().nextInt(10) + 1));

        } catch (FeignException e) {
            return Optional.empty();

        }
    }
}

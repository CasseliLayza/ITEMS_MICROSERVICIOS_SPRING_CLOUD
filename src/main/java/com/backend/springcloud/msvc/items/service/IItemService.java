package com.backend.springcloud.msvc.items.service;

import com.backend.lib.mcsv.commons.entity.Product;
import com.backend.springcloud.msvc.items.model.Item;

import java.util.List;
import java.util.Optional;

public interface IItemService {
    List<Item> findAll();

    Optional<Item> findById(Long id);

    Product save(Product product);

    Product update(Product product, Long id);

    void delete(Long id);
}

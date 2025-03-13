package com.backend.springcloud.msvc.items.controller;

import com.backend.springcloud.msvc.items.model.Item;
import com.backend.springcloud.msvc.items.service.IItemService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/items")
public class ItemController {

    private final IItemService itemService;

    public ItemController(@Qualifier("itemServiceWebClient") IItemService iItemService) {
        this.itemService = iItemService;
    }


    @GetMapping("/list")
    public List<Item> list() {
        return itemService.findAll();
    }


    @GetMapping("/find/{id}")
    public ResponseEntity<?> findItem(@PathVariable long id) {

        Optional<?> optionalItem = itemService.findById(id);

        return optionalItem.map(item -> new ResponseEntity<>(item, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(
                        Collections.singletonMap("message", "No existe el producto en el microservice msvc-productos")
                        , HttpStatus.NOT_FOUND));

    }
}

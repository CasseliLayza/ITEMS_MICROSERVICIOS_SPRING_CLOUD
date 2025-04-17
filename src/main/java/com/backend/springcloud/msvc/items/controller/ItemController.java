package com.backend.springcloud.msvc.items.controller;

import com.backend.springcloud.msvc.items.model.Item;
import com.backend.springcloud.msvc.items.model.Product;
import com.backend.springcloud.msvc.items.service.IItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/items")
@CrossOrigin(originPatterns = "*")
public class ItemController {

    private final Logger LOGGER = LoggerFactory.getLogger(ItemController.class);
    private final IItemService itemService;
    private final CircuitBreakerFactory circuitBreakerFactory;

    public ItemController(@Qualifier("itemServiceWebClient") IItemService iItemService, CircuitBreakerFactory circuitBreakerFactory) {
        this.itemService = iItemService;
        this.circuitBreakerFactory = circuitBreakerFactory;
    }


    @GetMapping("/list")
    public List<Item> list(@RequestParam(name = "name", required = false) String name,
                           @RequestHeader(name = "token-request", required = false) String token) {
        System.out.println("name = " + name);
        System.out.println("token-request = " + token);

        return itemService.findAll();
    }


    @GetMapping("/find/{id}")
    public ResponseEntity<?> findItem(@PathVariable long id) {

        Optional<?> optionalItem = circuitBreakerFactory.create("items").run(() -> itemService.findById(id), e -> {
            LOGGER.info("error: >>> " + e.getMessage());
            Product product = new Product();
            product.setName("Circuit Breaker");
            product.setCreateAt(LocalDate.now());
            product.setId(1L);
            product.setPrice(500.0);
            return Optional.of(new Item(product, 5));
        });

        return optionalItem.map(item -> new ResponseEntity<>(item, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(
                        Collections.singletonMap("message", "No existe el producto en el microservice msvc-productos")
                        , HttpStatus.NOT_FOUND));

    }
}

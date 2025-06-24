package com.backend.springcloud.msvc.items.service;

import com.backend.lib.mcsv.commons.entity.Product;
import com.backend.springcloud.msvc.items.model.Item;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;

@Service
//@Primary
public class ItemServiceWebClient implements IItemService {

    //private final WebClient.Builder client;
    private final WebClient client;

    public ItemServiceWebClient(WebClient client) {
        this.client = client;
    }


    @Override
    public List<Item> findAll() {
        System.out.println("webclientOK");

        //return client.build()
        return client
                .get()
                .uri("/list")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(Product.class)
                .map(product -> new Item(product, new Random().nextInt(10) + 1))
                .collectList()
                .block();
    }

    @Override
    public Optional<Item> findById(Long id) {
        System.out.println("webclientOK");
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);

        //try {
        //return Optional.ofNullable(client.build()
        return Optional.ofNullable(client
                .get()
                .uri("/find/{id}", params)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Product.class)
                .map(product -> new Item(product, new Random().nextInt(10) + 1))
                .block());
        //} catch (Exception e) {
        //    return Optional.empty();
        // }


    }

    @Override
    public Product save(Product product) {
        System.out.println("webclientOK");
        //return client.build()
        return client
                .post()
                .uri("/create")
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(product)
                .retrieve()
                .bodyToMono(Product.class)
                .block();
    }

    @Override
    public Product update(Product product, Long id) {
        System.out.println("webclientOK");
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);

        return client
                .put()
                .uri("/update/{id}", params)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(product)
                .retrieve()
                .bodyToMono(Product.class)
                .block();
    }

    @Override
    public void delete(Long id) {
        System.out.println("webclientOK");
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);

        client
                .delete()
                .uri("/delete/{id}", params)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Product.class)
                .block();
    }
}

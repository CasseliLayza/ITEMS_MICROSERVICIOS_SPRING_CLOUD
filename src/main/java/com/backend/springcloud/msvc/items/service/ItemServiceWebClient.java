package com.backend.springcloud.msvc.items.service;

import com.backend.springcloud.msvc.items.model.Item;
import com.backend.springcloud.msvc.items.model.Product;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;

@Service
//@Primary
public class ItemServiceWebClient implements IItemService {

    private final WebClient.Builder client;

    public ItemServiceWebClient(WebClient.Builder client) {
        this.client = client;
    }


    @Override
    public List<Item> findAll() {
        System.out.println("webclientOK");

        return client.build()
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
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);

        try {
            return Optional.ofNullable(client.build()
                    .get()
                    .uri("/find/{id}", params)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(Product.class)
                    .map(product -> new Item(product, new Random().nextInt(10) + 1))
                    .block());
        } catch (Exception e) {
            return Optional.empty();
        }


    }
}

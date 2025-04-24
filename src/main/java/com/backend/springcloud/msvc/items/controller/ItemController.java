package com.backend.springcloud.msvc.items.controller;

import com.backend.springcloud.msvc.items.model.Item;
import com.backend.springcloud.msvc.items.model.Product;
import com.backend.springcloud.msvc.items.service.IItemService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/items")
@CrossOrigin(originPatterns = "*")
@RefreshScope
public class ItemController {

    private final Logger LOGGER = LoggerFactory.getLogger(ItemController.class);
    private final IItemService itemService;
    private final CircuitBreakerFactory circuitBreakerFactory;
    @Value("${configuracion.texto}")
    private String texto;

    @Autowired
    private Environment environment;

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
            LOGGER.error("error: >>> \t" + e.getMessage());
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


    @GetMapping("/find/details/{id}")
    @CircuitBreaker(name = "items", fallbackMethod = "handlerFallbackMethodProduct")
    public ResponseEntity<?> findItemDetails(@PathVariable long id) {

        Optional<?> optionalItem = itemService.findById(id);

        return optionalItem.map(item -> new ResponseEntity<>(item, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(
                        Collections.singletonMap("message", "No existe el producto en el microservice msvc-productos")
                        , HttpStatus.NOT_FOUND));

    }

    public ResponseEntity<?> handlerFallbackMethodProduct(Throwable e) {

        LOGGER.error("error: >>> \t" + e.getMessage());
        Product product = new Product();
        product.setName("Circuit Breaker");
        product.setCreateAt(LocalDate.now());
        product.setId(1L);
        product.setPrice(500.0);
        return ResponseEntity.ok(new Item(product, 5));

    }

    @GetMapping("/find/details2/{id}")
    @CircuitBreaker(name = "items", fallbackMethod = "handlerFallbackMethodProduct2")
    @TimeLimiter(name = "items")
    public CompletableFuture<?> findItemDetails2(@PathVariable long id) {

        return CompletableFuture.supplyAsync(() -> {
            Optional<?> optionalItem = itemService.findById(id);

            return optionalItem.map(item -> new ResponseEntity<>(item, HttpStatus.OK))
                    .orElseGet(() -> new ResponseEntity<>(
                            Collections.singletonMap("message", "No existe el producto en el microservice msvc-productos")
                            , HttpStatus.NOT_FOUND));
        });
    }


    public CompletableFuture<?> handlerFallbackMethodProduct2(Throwable e) {

        return CompletableFuture.supplyAsync(() -> {
            LOGGER.error("error: >>> \t " + e.getMessage());
            Product product = new Product();
            product.setName("Circuit Breaker");
            product.setCreateAt(LocalDate.now());
            product.setId(1L);
            product.setPrice(500.0);
            return ResponseEntity.ok(new Item(product, 5));
        });


    }

    @PostMapping("/save")
    public ResponseEntity<Product> save(@RequestBody Product product) {
        return ResponseEntity.status(HttpStatus.CREATED).body(itemService.save(product));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Product> update(@RequestBody Product product, @PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.CREATED).body(itemService.update(product, id));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        itemService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(Collections.singletonMap("message", "Producto eliminado correctamente"));
    }

    @GetMapping("/fetch-configs")
    public ResponseEntity<?> fetchConfigs(@Value("${server.port}") String port) {
        Map<String, String> jsonConfigs = new HashMap<>();
        jsonConfigs.put("texto", texto);
        jsonConfigs.put("port", port);

        LOGGER.info("Texto: " + texto);
        LOGGER.info("Port: " + port);

        if (environment.getActiveProfiles().length > 0 && environment.getActiveProfiles()[0].equals("dev")) {
            jsonConfigs.put("autor.nombre", environment.getProperty("configuracion.autor.nombre"));
            jsonConfigs.put("autor.email", environment.getProperty("configuracion.autor.email"));

        }
        return ResponseEntity.ok(jsonConfigs);
    }


}

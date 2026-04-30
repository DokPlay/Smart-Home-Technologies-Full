package com.smarthome.commerce.store.controller;

import com.smarthome.commerce.api.dto.Availability;
import com.smarthome.commerce.api.dto.ProductDto;
import com.smarthome.commerce.api.dto.State;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final Map<Long, ProductDto> products = new HashMap<>();

    public ProductController() {
        products.put(1L, new ProductDto(1L, "Smart Hub", "Core controller for Smart Home", "CONTROL", BigDecimal.valueOf(199.99), Availability.MANY, State.ACTIVE, List.of()));
        products.put(2L, new ProductDto(2L, "Temperature Sensor", "Wireless temperature sensor", "SENSORS", BigDecimal.valueOf(29.99), Availability.ENOUGH, State.ACTIVE, List.of()));
    }

    @GetMapping
    public Collection<ProductDto> list() {
        return products.values();
    }

    @GetMapping("/{id}")
    public ProductDto get(@PathVariable Long id) {
        return products.get(id);
    }

    @PostMapping("/admin")
    public ProductDto create(@RequestBody ProductDto dto) {
        long id = products.keySet().stream().mapToLong(Long::longValue).max().orElse(0L) + 1;
        ProductDto created = new ProductDto(id, dto.name(), dto.description(), dto.category(), dto.price(), dto.availability(), dto.state(), dto.photos());
        products.put(id, created);
        return created;
    }

    @DeleteMapping("/admin/{id}")
    public void deactivate(@PathVariable Long id) {
        var p = products.get(id);
        if (p != null) {
            products.put(id, new ProductDto(p.id(), p.name(), p.description(), p.category(), p.price(), p.availability(), State.DEACTIVATE, p.photos()));
        }
    }
}

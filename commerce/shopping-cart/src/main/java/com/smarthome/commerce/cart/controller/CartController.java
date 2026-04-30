package com.smarthome.commerce.cart.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    // Простой in-memory корзины: username -> (productId -> qty)
    private final Map<String, Map<Long, Integer>> carts = new HashMap<>();

    @PostMapping("/{username}/items")
    public ResponseEntity<?> addItem(@PathVariable String username, @RequestBody Map<String, Object> body) {
        // Ожидается { "productId": 1, "quantity": 2 }
        Long productId = ((Number) body.getOrDefault("productId", 0)).longValue();
        Integer qty = ((Number) body.getOrDefault("quantity", 1)).intValue();

        var userCart = carts.computeIfAbsent(username, u -> new HashMap<>());
        userCart.merge(productId, qty, Integer::sum);
        return ResponseEntity.ok(userCart);
    }

    @GetMapping("/{username}")
    public ResponseEntity<?> getCart(@PathVariable String username) {
        return ResponseEntity.ok(carts.getOrDefault(username, Map.of()));
    }

    @PostMapping("/{username}/deactivate")
    public ResponseEntity<?> deactivate(@PathVariable String username) {
        // перевод корзины в неактивное состояние — простая заглушка
        // в реальности нужно хранить флаг
        return ResponseEntity.ok(Map.of("status", "deactivated"));
    }
}

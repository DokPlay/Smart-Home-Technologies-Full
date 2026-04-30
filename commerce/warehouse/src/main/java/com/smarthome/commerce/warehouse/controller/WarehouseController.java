package com.smarthome.commerce.warehouse.controller;

import com.smarthome.commerce.api.dto.WarehouseAddress;
import com.smarthome.commerce.api.warehouse.WarehouseApi;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/warehouse")
public class WarehouseController implements WarehouseApi {
    private static final String[] ADDRESSES = new String[]{"ADDRESS_1","ADDRESS_2"};
    private static final String CURRENT_ADDRESS = ADDRESSES[new SecureRandom().nextInt(ADDRESSES.length)];

    // Простейшая in-memory "складская" информация: productId -> available quantity
    private final Map<Long, Integer> stock = new HashMap<>();

    public WarehouseController() {
        stock.put(1L, 200);
        stock.put(2L, 50);
    }

    @Override
    public Map<Long, String> checkAvailability(Map<Long, Integer> items) {
        Map<Long, String> result = new HashMap<>();
        for (var e : items.entrySet()) {
            long id = e.getKey();
            int requested = e.getValue();
            int available = stock.getOrDefault(id, 0);
            if (available >= requested) {
                result.put(id, "OK");
            } else {
                result.put(id, "MISSING: need=" + requested + ", have=" + available);
            }
        }
        return result;
    }

    @Override
    public WarehouseAddress getAddress() {
        return new WarehouseAddress(CURRENT_ADDRESS, CURRENT_ADDRESS, CURRENT_ADDRESS, CURRENT_ADDRESS, CURRENT_ADDRESS);
    }

    // admin helper
    public Map<String, Object> addStock(Map<String, Object> body) {
        Long productId = ((Number) body.getOrDefault("productId", 0)).longValue();
        Integer qty = ((Number) body.getOrDefault("quantity", 0)).intValue();
        stock.merge(productId, qty, Integer::sum);
        return Map.of("productId", productId, "quantity", stock.get(productId));
    }
}

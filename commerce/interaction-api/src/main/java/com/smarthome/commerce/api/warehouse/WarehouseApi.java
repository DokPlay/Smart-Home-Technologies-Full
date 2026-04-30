package com.smarthome.commerce.api.warehouse;

import com.smarthome.commerce.api.dto.WarehouseAddress;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@RequestMapping("/api/warehouse")
public interface WarehouseApi {

    @PostMapping("/check")
    Map<Long, String> checkAvailability(@RequestBody Map<Long, Integer> items);

    @GetMapping("/address")
    WarehouseAddress getAddress();
}

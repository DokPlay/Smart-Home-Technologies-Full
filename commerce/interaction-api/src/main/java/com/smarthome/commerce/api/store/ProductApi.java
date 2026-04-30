package com.smarthome.commerce.api.store;

import java.util.Collection;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smarthome.commerce.api.dto.ProductDto;

@RequestMapping("/api/products")
public interface ProductApi {

    @GetMapping
    Collection<ProductDto> list(@RequestParam(value = "category", required = false) String category);

    @GetMapping("/{id}")
    ProductDto get(@PathVariable Long id);
}

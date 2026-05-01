package com.smarthome.commerce.cart.feign;

import org.springframework.cloud.openfeign.FeignClient;

import com.smarthome.commerce.api.warehouse.WarehouseApi;

@FeignClient(name = "warehouse", fallbackFactory = WarehouseFeignFallbackFactory.class)
public interface WarehouseFeignClient extends WarehouseApi {
}

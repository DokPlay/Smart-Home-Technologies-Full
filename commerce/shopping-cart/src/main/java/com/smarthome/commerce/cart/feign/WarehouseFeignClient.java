package com.smarthome.commerce.cart.feign;

import org.springframework.cloud.openfeign.FeignClient;

import com.smarthome.commerce.api.warehouse.WarehouseApi;

@FeignClient(name = "warehouse")
public interface WarehouseFeignClient extends WarehouseApi {
}

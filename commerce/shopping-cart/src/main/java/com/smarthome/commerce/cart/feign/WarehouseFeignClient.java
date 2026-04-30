package com.smarthome.commerce.cart.feign;

import com.smarthome.commerce.api.warehouse.WarehouseApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "warehouse")
public interface WarehouseFeignClient extends WarehouseApi {
}

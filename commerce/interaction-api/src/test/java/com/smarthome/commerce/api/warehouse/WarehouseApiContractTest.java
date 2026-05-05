package com.smarthome.commerce.api.warehouse;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;

import org.junit.jupiter.api.Test;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

class WarehouseApiContractTest {

    @Test
    void warehouseApiDeclaresFeignContractWithBasePathAndFallbackFactory() {
        FeignClient feignClient = WarehouseApi.class.getAnnotation(FeignClient.class);

        assertThat(feignClient).isNotNull();
        assertThat(feignClient.name()).isEqualTo("warehouse");
        assertThat(feignClient.path()).isEqualTo("/api/v1/warehouse");
        assertThat(feignClient.fallbackFactory()).isEqualTo(WarehouseFeignFallbackFactory.class);
    }

    @Test
    void warehouseApiUsesRelativeMappingsInsideFeignBasePath() throws NoSuchMethodException {
        Method createProduct = WarehouseApi.class.getMethod(
                "newProductInWarehouse",
                NewProductInWarehouseRequest.class
        );
        Method checkStock = WarehouseApi.class.getMethod(
                "checkProductQuantityEnoughForShoppingCart",
                com.smarthome.commerce.api.cart.ShoppingCartDto.class
        );
        Method addStock = WarehouseApi.class.getMethod(
                "addProductToWarehouse",
                AddProductToWarehouseRequest.class
        );
        Method address = WarehouseApi.class.getMethod("getWarehouseAddress");

        assertThat(createProduct.getAnnotation(PutMapping.class).value()).isEmpty();
        assertThat(checkStock.getAnnotation(PostMapping.class).value()).containsExactly("/check");
        assertThat(addStock.getAnnotation(PostMapping.class).value()).containsExactly("/add");
        assertThat(address.getAnnotation(GetMapping.class).value()).containsExactly("/address");
    }
}

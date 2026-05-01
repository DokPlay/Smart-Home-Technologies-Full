package com.smarthome.commerce.cart.controller;

import static org.hamcrest.Matchers.anEmptyMap;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smarthome.commerce.api.cart.ChangeProductQuantityRequest;
import com.smarthome.commerce.api.cart.ShoppingCartDto;
import com.smarthome.commerce.api.warehouse.BookedProductsDto;
import com.smarthome.commerce.cart.exception.WarehouseServiceUnavailableException;
import com.smarthome.commerce.cart.feign.WarehouseFeignClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(properties = {
        "spring.cloud.config.enabled=false",
        "eureka.client.enabled=false",
        "spring.datasource.url=jdbc:h2:mem:shopping-cart-controller-test;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.open-in-view=false"
})
@AutoConfigureMockMvc
class CartControllerContractTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private WarehouseFeignClient warehouseFeignClient;

    @Test
    void openApiCartRoutesCreateModifyRemoveAndDeactivateCart() throws Exception {
        UUID productId = UUID.randomUUID();
        when(warehouseFeignClient.checkProductQuantityEnoughForShoppingCart(any(ShoppingCartDto.class)))
                .thenReturn(new BookedProductsDto(2.0, 4.0, false));

        mockMvc.perform(get("/api/v1/shopping-cart")
                        .param("username", "alice"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shoppingCartId").isNotEmpty())
                .andExpect(jsonPath("$.products", anEmptyMap()));

        mockMvc.perform(put("/api/v1/shopping-cart")
                        .param("username", "alice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(productId, 2L))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.products['%s']", productId).value(2));

        mockMvc.perform(post("/api/v1/shopping-cart/change-quantity")
                        .param("username", "alice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new ChangeProductQuantityRequest(productId, 1L)
                        )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.products['%s']", productId).value(1));

        mockMvc.perform(post("/api/v1/shopping-cart/remove")
                        .param("username", "alice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of(productId))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.products", anEmptyMap()));

        mockMvc.perform(delete("/api/v1/shopping-cart")
                        .param("username", "alice"))
                .andExpect(status().isOk());

        mockMvc.perform(put("/api/v1/shopping-cart")
                        .param("username", "alice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(UUID.randomUUID(), 1L))))
                .andExpect(status().isConflict());
    }

    @Test
    void addProductReturnsServiceUnavailableWhenWarehouseCannotBeReached() throws Exception {
        UUID productId = UUID.randomUUID();
        when(warehouseFeignClient.checkProductQuantityEnoughForShoppingCart(any(ShoppingCartDto.class)))
                .thenThrow(new WarehouseServiceUnavailableException(new IllegalStateException("warehouse is down")));

        mockMvc.perform(put("/api/v1/shopping-cart")
                        .param("username", "alice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(productId, 1L))))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.error").value("WAREHOUSE_UNAVAILABLE"))
                .andExpect(jsonPath("$.message").value(
                        "Warehouse service is temporarily unavailable. Please try again later."
                ));
    }
}

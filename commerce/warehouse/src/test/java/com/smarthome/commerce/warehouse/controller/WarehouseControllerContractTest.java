package com.smarthome.commerce.warehouse.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smarthome.commerce.api.cart.ShoppingCartDto;
import com.smarthome.commerce.api.warehouse.AddProductToWarehouseRequest;
import com.smarthome.commerce.api.warehouse.DimensionDto;
import com.smarthome.commerce.api.warehouse.NewProductInWarehouseRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(properties = {
        "spring.cloud.config.enabled=false",
        "eureka.client.enabled=false",
        "spring.datasource.url=jdbc:h2:mem:warehouse-controller-test;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.open-in-view=false"
})
@AutoConfigureMockMvc
class WarehouseControllerContractTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void openApiWarehouseRoutesRegisterStockCheckAndReturnAddress() throws Exception {
        UUID productId = UUID.randomUUID();

        mockMvc.perform(put("/api/v1/warehouse")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new NewProductInWarehouseRequest(
                                productId,
                                true,
                                new DimensionDto(2.0, 3.0, 4.0),
                                1.5
                        ))))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/v1/warehouse/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new AddProductToWarehouseRequest(productId, 3L)
                        )))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/v1/warehouse/check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new ShoppingCartDto(UUID.randomUUID(), Map.of(productId, 2L))
                        )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deliveryWeight").value(3.0))
                .andExpect(jsonPath("$.deliveryVolume").value(48.0))
                .andExpect(jsonPath("$.fragile").value(true));

        mockMvc.perform(get("/api/v1/warehouse/address"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.country").isNotEmpty())
                .andExpect(jsonPath("$.city").isNotEmpty())
                .andExpect(jsonPath("$.street").isNotEmpty())
                .andExpect(jsonPath("$.house").isNotEmpty())
                .andExpect(jsonPath("$.flat").isNotEmpty());
    }
}

package com.smarthome.commerce.store.controller;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smarthome.commerce.api.store.ProductCategory;
import com.smarthome.commerce.api.store.ProductDto;
import com.smarthome.commerce.api.store.ProductState;
import com.smarthome.commerce.api.store.QuantityState;
import com.smarthome.commerce.api.store.SetProductQuantityStateRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(properties = {
        "spring.cloud.config.enabled=false",
        "eureka.client.enabled=false",
        "spring.datasource.url=jdbc:h2:mem:shopping-store-controller-test;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.open-in-view=false"
})
@AutoConfigureMockMvc
class ProductControllerContractTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void openApiProductRoutesCreateListUpdateQuantityAndSoftDeleteProduct() throws Exception {
        UUID productId = UUID.randomUUID();
        ProductDto product = new ProductDto(
                productId,
                "Smart Hub Pro",
                "Controller",
                "https://example.test/hub.png",
                QuantityState.MANY,
                ProductState.ACTIVE,
                4.9,
                ProductCategory.CONTROL,
                BigDecimal.valueOf(299.90)
        );

        mockMvc.perform(put("/api/v1/shopping-store")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(productId.toString()));

        ProductDto updatedProduct = new ProductDto(
                productId,
                "Smart Hub Pro Updated",
                "Updated controller",
                "https://example.test/hub-updated.png",
                QuantityState.ENOUGH,
                ProductState.ACTIVE,
                4.7,
                ProductCategory.CONTROL,
                BigDecimal.valueOf(249.90)
        );
        mockMvc.perform(post("/api/v1/shopping-store")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedProduct)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productName").value("Smart Hub Pro Updated"))
                .andExpect(jsonPath("$.price").value(249.90));

        mockMvc.perform(get("/api/v1/shopping-store")
                        .param("category", "CONTROL")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].productId", hasItem(productId.toString())));

        mockMvc.perform(post("/api/v1/shopping-store/quantityState")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new SetProductQuantityStateRequest(productId, QuantityState.FEW)
                        )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));

        mockMvc.perform(post("/api/v1/shopping-store/removeProductFromStore")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));

        mockMvc.perform(get("/api/v1/shopping-store/{productId}", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productState").value("DEACTIVATE"));
    }
}

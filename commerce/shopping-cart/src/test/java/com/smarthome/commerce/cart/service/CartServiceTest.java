package com.smarthome.commerce.cart.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.smarthome.commerce.api.cart.ChangeProductQuantityRequest;
import com.smarthome.commerce.api.cart.ShoppingCartDto;
import com.smarthome.commerce.api.warehouse.BookedProductsDto;
import com.smarthome.commerce.cart.exception.CartInactiveException;
import com.smarthome.commerce.cart.exception.InvalidShoppingCartRequestException;
import com.smarthome.commerce.cart.exception.NoProductsInShoppingCartException;
import com.smarthome.commerce.cart.feign.WarehouseFeignClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest(properties = {
        "spring.cloud.config.enabled=false",
        "eureka.client.enabled=false",
        "spring.datasource.url=jdbc:h2:mem:shopping-cart-test;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.open-in-view=false"
})
class CartServiceTest {

    @Autowired
    private CartService cartService;

    @MockBean
    private WarehouseFeignClient warehouseFeignClient;

    @Test
    void addProductChecksWarehouseAndStoresProductInCart() {
        UUID productId = UUID.randomUUID();
        when(warehouseFeignClient.checkProductQuantityEnoughForShoppingCart(argThat(cart ->
                cart != null && cart.products().get(productId).equals(2L)
        ))).thenReturn(new BookedProductsDto(2.0, 4.0, false));

        ShoppingCartDto cart = cartService.addProductToShoppingCart("alice", Map.of(productId, 2L));

        assertThat(cart.shoppingCartId()).isNotNull();
        assertThat(cart.products()).containsEntry(productId, 2L);
        verify(warehouseFeignClient).checkProductQuantityEnoughForShoppingCart(argThat(checkedCart ->
                checkedCart.products().get(productId).equals(2L)
        ));
    }

    @Test
    void addingSameProductChecksWarehouseForTotalQuantity() {
        UUID productId = UUID.randomUUID();
        when(warehouseFeignClient.checkProductQuantityEnoughForShoppingCart(argThat(cart ->
                cart != null && cart.products().get(productId).equals(2L)
        ))).thenReturn(new BookedProductsDto(2.0, 4.0, false));
        when(warehouseFeignClient.checkProductQuantityEnoughForShoppingCart(argThat(cart ->
                cart != null && cart.products().get(productId).equals(5L)
        ))).thenReturn(new BookedProductsDto(5.0, 10.0, false));

        cartService.addProductToShoppingCart("bob", Map.of(productId, 2L));
        ShoppingCartDto cart = cartService.addProductToShoppingCart("bob", Map.of(productId, 3L));

        assertThat(cart.products()).containsEntry(productId, 5L);
        verify(warehouseFeignClient).checkProductQuantityEnoughForShoppingCart(argThat(checkedCart ->
                checkedCart != null && checkedCart.products().get(productId).equals(5L)
        ));
    }

    @Test
    void addProductRejectsNonPositiveQuantityBeforeWarehouseCall() {
        UUID productId = UUID.randomUUID();

        assertThatThrownBy(() -> cartService.addProductToShoppingCart("alice", Map.of(productId, 0L)))
                .isInstanceOf(InvalidShoppingCartRequestException.class);

        verifyNoInteractions(warehouseFeignClient);
    }

    @Test
    void changeProductQuantityRejectsMissingQuantityBeforeWarehouseCall() {
        UUID productId = UUID.randomUUID();
        when(warehouseFeignClient.checkProductQuantityEnoughForShoppingCart(argThat(cart ->
                cart != null && cart.products().get(productId).equals(1L)
        ))).thenReturn(new BookedProductsDto(1.0, 1.0, false));
        cartService.addProductToShoppingCart("erin", Map.of(productId, 1L));
        reset(warehouseFeignClient);

        assertThatThrownBy(() -> cartService.changeProductQuantity(
                "erin",
                new ChangeProductQuantityRequest(productId, null)
        ))
                .isInstanceOf(InvalidShoppingCartRequestException.class);

        verifyNoInteractions(warehouseFeignClient);
    }

    @Test
    void deactivatedCartCannotBeModified() {
        UUID productId = UUID.randomUUID();
        when(warehouseFeignClient.checkProductQuantityEnoughForShoppingCart(argThat(cart ->
                cart != null && cart.products().get(productId).equals(1L)
        ))).thenReturn(new BookedProductsDto(1.0, 1.0, false));
        cartService.addProductToShoppingCart("carol", Map.of(productId, 1L));

        cartService.deactivateCurrentShoppingCart("carol");

        assertThatThrownBy(() -> cartService.addProductToShoppingCart("carol", Map.of(UUID.randomUUID(), 1L)))
                .isInstanceOf(CartInactiveException.class);
    }

    @Test
    void removingMissingProductReportsCartProblem() {
        cartService.getShoppingCart("dave");

        assertThatThrownBy(() -> cartService.removeFromShoppingCart("dave", List.of(UUID.randomUUID())))
                .isInstanceOf(NoProductsInShoppingCartException.class);
    }
}

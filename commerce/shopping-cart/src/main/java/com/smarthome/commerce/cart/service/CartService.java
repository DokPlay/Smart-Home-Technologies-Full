package com.smarthome.commerce.cart.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.smarthome.commerce.api.cart.ChangeProductQuantityRequest;
import com.smarthome.commerce.api.cart.ShoppingCartDto;
import com.smarthome.commerce.api.warehouse.WarehouseApi;
import com.smarthome.commerce.cart.exception.CartInactiveException;
import com.smarthome.commerce.cart.exception.InvalidShoppingCartRequestException;
import com.smarthome.commerce.cart.exception.NoProductsInShoppingCartException;
import com.smarthome.commerce.cart.exception.NotAuthorizedUserException;
import com.smarthome.commerce.cart.mapper.ShoppingCartMapper;
import com.smarthome.commerce.cart.model.ShoppingCartEntity;
import com.smarthome.commerce.cart.repository.ShoppingCartRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CartService {

    private final ShoppingCartRepository shoppingCartRepository;
    private final ShoppingCartMapper shoppingCartMapper;
    private final WarehouseApi warehouseApi;

    public CartService(ShoppingCartRepository shoppingCartRepository, ShoppingCartMapper shoppingCartMapper,
                       WarehouseApi warehouseApi) {
        this.shoppingCartRepository = shoppingCartRepository;
        this.shoppingCartMapper = shoppingCartMapper;
        this.warehouseApi = warehouseApi;
    }

    @Transactional
    public ShoppingCartDto getShoppingCart(String username) {
        return shoppingCartMapper.toDto(getOrCreateCart(username));
    }

    @Transactional
    public ShoppingCartDto addProductToShoppingCart(String username, Map<UUID, Long> products) {
        ShoppingCartEntity cart = getOrCreateCart(username);
        requireActive(cart);
        validateProducts(products);

        Map<UUID, Long> projectedProducts = currentProducts(cart);
        products.forEach((productId, quantity) -> projectedProducts.merge(productId, quantity, Long::sum));
        warehouseApi.checkProductQuantityEnoughForShoppingCart(
                new ShoppingCartDto(cart.getShoppingCartId(), projectedProducts)
        );

        products.forEach(cart::addItem);
        return shoppingCartMapper.toDto(cart);
    }

    @Transactional
    public void deactivateCurrentShoppingCart(String username) {
        ShoppingCartEntity cart = getOrCreateCart(username);
        cart.deactivate();
    }

    @Transactional
    public ShoppingCartDto removeFromShoppingCart(String username, List<UUID> products) {
        ShoppingCartEntity cart = getOrCreateCart(username);
        requireActive(cart);

        for (UUID productId : products) {
            if (!currentProducts(cart).containsKey(productId)) {
                throw new NoProductsInShoppingCartException("Product is not in shopping cart: " + productId);
            }
            cart.removeItem(productId);
        }
        return shoppingCartMapper.toDto(cart);
    }

    @Transactional
    public ShoppingCartDto changeProductQuantity(String username, ChangeProductQuantityRequest request) {
        ShoppingCartEntity cart = getOrCreateCart(username);
        requireActive(cart);
        validateChangeQuantityRequest(request);

        Map<UUID, Long> projectedProducts = currentProducts(cart);
        if (!projectedProducts.containsKey(request.productId())) {
            throw new NoProductsInShoppingCartException("Product is not in shopping cart: " + request.productId());
        }
        if (request.newQuantity() <= 0) {
            cart.removeItem(request.productId());
            return shoppingCartMapper.toDto(cart);
        }

        projectedProducts.put(request.productId(), request.newQuantity());
        warehouseApi.checkProductQuantityEnoughForShoppingCart(
                new ShoppingCartDto(cart.getShoppingCartId(), projectedProducts)
        );
        cart.setItemQuantity(request.productId(), request.newQuantity());
        return shoppingCartMapper.toDto(cart);
    }

    private ShoppingCartEntity getOrCreateCart(String username) {
        validateUsername(username);
        return shoppingCartRepository.findByUsername(username)
                .orElseGet(() -> shoppingCartRepository.save(
                        new ShoppingCartEntity(UUID.randomUUID(), username, true)
                ));
    }

    private Map<UUID, Long> currentProducts(ShoppingCartEntity cart) {
        Map<UUID, Long> products = new LinkedHashMap<>();
        cart.getItems().forEach(item -> products.put(item.getProductId(), item.getQuantity()));
        return products;
    }

    private void requireActive(ShoppingCartEntity cart) {
        if (!cart.isActive()) {
            throw new CartInactiveException(cart.getUsername());
        }
    }

    private void validateUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new NotAuthorizedUserException();
        }
    }

    private void validateProducts(Map<UUID, Long> products) {
        if (products == null || products.isEmpty()) {
            throw new InvalidShoppingCartRequestException("Products map must not be empty");
        }
        products.forEach((productId, quantity) -> {
            if (productId == null) {
                throw new InvalidShoppingCartRequestException("Product id must not be null");
            }
            if (quantity == null || quantity <= 0) {
                throw new InvalidShoppingCartRequestException("Product quantity must be positive: " + productId);
            }
        });
    }

    private void validateChangeQuantityRequest(ChangeProductQuantityRequest request) {
        if (request == null) {
            throw new InvalidShoppingCartRequestException("Change quantity request must not be null");
        }
        if (request.productId() == null) {
            throw new InvalidShoppingCartRequestException("Product id must not be null");
        }
        if (request.newQuantity() == null) {
            throw new InvalidShoppingCartRequestException("Product quantity must not be null: " + request.productId());
        }
    }
}

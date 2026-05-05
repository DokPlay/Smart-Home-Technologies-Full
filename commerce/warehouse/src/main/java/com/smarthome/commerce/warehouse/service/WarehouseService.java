package com.smarthome.commerce.warehouse.service;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.smarthome.commerce.api.cart.ShoppingCartDto;
import com.smarthome.commerce.api.warehouse.AddProductToWarehouseRequest;
import com.smarthome.commerce.api.warehouse.AddressDto;
import com.smarthome.commerce.api.warehouse.BookedProductsDto;
import com.smarthome.commerce.api.warehouse.DimensionDto;
import com.smarthome.commerce.api.warehouse.NewProductInWarehouseRequest;
import com.smarthome.commerce.warehouse.exception.InvalidWarehouseProductRequestException;
import com.smarthome.commerce.warehouse.exception.InvalidWarehouseProductQuantityException;
import com.smarthome.commerce.warehouse.exception.NoSpecifiedProductInWarehouseException;
import com.smarthome.commerce.warehouse.exception.ProductInShoppingCartLowQuantityInWarehouseException;
import com.smarthome.commerce.warehouse.exception.SpecifiedProductAlreadyInWarehouseException;
import com.smarthome.commerce.warehouse.model.WarehouseProductEntity;
import com.smarthome.commerce.warehouse.repository.WarehouseProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WarehouseService {

    private static final String[] ADDRESSES = new String[]{"ADDRESS_1", "ADDRESS_2"};
    private static final String CURRENT_ADDRESS = ADDRESSES[new SecureRandom().nextInt(ADDRESSES.length)];

    private final WarehouseProductRepository warehouseProductRepository;

    public WarehouseService(WarehouseProductRepository warehouseProductRepository) {
        this.warehouseProductRepository = warehouseProductRepository;
    }

    @Transactional
    public void newProductInWarehouse(NewProductInWarehouseRequest request) {
        validateNewProductRequest(request);
        if (warehouseProductRepository.existsById(request.productId())) {
            throw new SpecifiedProductAlreadyInWarehouseException(request.productId());
        }
        warehouseProductRepository.save(new WarehouseProductEntity(
                request.productId(),
                Boolean.TRUE.equals(request.fragile()),
                request.dimension().width(),
                request.dimension().height(),
                request.dimension().depth(),
                request.weight(),
                0L
        ));
    }

    @Transactional
    public void addProductToWarehouse(AddProductToWarehouseRequest request) {
        if (request == null || request.productId() == null) {
            throw new InvalidWarehouseProductRequestException("Product id must not be null");
        }
        if (request.quantity() == null || request.quantity() <= 0) {
            throw new InvalidWarehouseProductQuantityException("Product quantity must be positive");
        }
        WarehouseProductEntity product = findProduct(request.productId());
        product.addQuantity(request.quantity());
    }

    @Transactional(readOnly = true)
    public BookedProductsDto checkProductQuantityEnoughForShoppingCart(ShoppingCartDto shoppingCart) {
        List<String> missing = new ArrayList<>();
        double deliveryWeight = 0.0;
        double deliveryVolume = 0.0;
        boolean fragile = false;

        for (var item : shoppingCart.products().entrySet()) {
            UUID productId = item.getKey();
            long requested = item.getValue();
            WarehouseProductEntity product = warehouseProductRepository.findById(productId).orElse(null);

            if (product == null || product.getQuantity() < requested) {
                long available = product == null ? 0L : product.getQuantity();
                missing.add(productId + " requested=" + requested + " available=" + available);
                continue;
            }

            deliveryWeight += product.getWeight() * requested;
            deliveryVolume += product.getWidth() * product.getHeight() * product.getDepth() * requested;
            fragile = fragile || product.isFragile();
        }

        if (!missing.isEmpty()) {
            throw new ProductInShoppingCartLowQuantityInWarehouseException(
                    "Not enough products in warehouse: " + String.join("; ", missing)
            );
        }

        return new BookedProductsDto(deliveryWeight, deliveryVolume, fragile);
    }

    public AddressDto getWarehouseAddress() {
        return new AddressDto(CURRENT_ADDRESS, CURRENT_ADDRESS, CURRENT_ADDRESS, CURRENT_ADDRESS, CURRENT_ADDRESS);
    }

    private WarehouseProductEntity findProduct(UUID productId) {
        return warehouseProductRepository.findById(productId)
                .orElseThrow(() -> new NoSpecifiedProductInWarehouseException(productId));
    }

    private void validateNewProductRequest(NewProductInWarehouseRequest request) {
        if (request == null || request.productId() == null) {
            throw new InvalidWarehouseProductRequestException("Product id must not be null");
        }
        DimensionDto dimension = request.dimension();
        if (dimension == null || dimension.width() == null || dimension.height() == null || dimension.depth() == null) {
            throw new InvalidWarehouseProductRequestException("Product dimensions must be filled");
        }
        if (dimension.width() <= 0 || dimension.height() <= 0 || dimension.depth() <= 0) {
            throw new InvalidWarehouseProductRequestException("Product dimensions must be positive");
        }
        if (request.weight() == null || request.weight() <= 0) {
            throw new InvalidWarehouseProductRequestException("Product weight must be positive");
        }
    }
}

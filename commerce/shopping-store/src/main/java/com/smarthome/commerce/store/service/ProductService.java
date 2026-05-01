package com.smarthome.commerce.store.service;

import java.util.Collection;
import java.util.UUID;

import com.smarthome.commerce.api.store.ProductCategory;
import com.smarthome.commerce.api.store.ProductDto;
import com.smarthome.commerce.api.store.ProductState;
import com.smarthome.commerce.api.store.SetProductQuantityStateRequest;
import com.smarthome.commerce.store.exception.ProductNotFoundException;
import com.smarthome.commerce.store.mapper.ProductMapper;
import com.smarthome.commerce.store.model.ProductEntity;
import com.smarthome.commerce.store.repository.ProductRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public ProductService(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    @Transactional(readOnly = true)
    public Collection<ProductDto> getProducts(ProductCategory category, Pageable pageable) {
        Pageable effectivePageable = pageable == null ? PageRequest.of(0, 20) : pageable;
        return productRepository.findByProductCategoryAndProductState(
                        category,
                        ProductState.ACTIVE,
                        effectivePageable
                ).stream()
                .map(productMapper::toDto)
                .toList();
    }

    @Transactional
    public ProductDto createNewProduct(ProductDto product) {
        ProductEntity entity = productMapper.toEntity(product);
        entity.setProductId(product.productId() == null ? UUID.randomUUID() : product.productId());
        if (entity.getProductState() == null) {
            entity.setProductState(ProductState.ACTIVE);
        }
        return productMapper.toDto(productRepository.save(entity));
    }

    @Transactional
    public ProductDto updateProduct(ProductDto product) {
        ProductEntity stored = findProduct(product.productId());
        stored.setProductName(product.productName());
        stored.setDescription(product.description());
        stored.setImageSrc(product.imageSrc());
        stored.setQuantityState(product.quantityState());
        stored.setProductState(product.productState());
        stored.setRating(product.rating());
        stored.setProductCategory(product.productCategory());
        stored.setPrice(product.price());
        return productMapper.toDto(stored);
    }

    @Transactional
    public Boolean removeProductFromStore(UUID productId) {
        ProductEntity product = findProduct(productId);
        product.setProductState(ProductState.DEACTIVATE);
        return true;
    }

    @Transactional
    public Boolean setProductQuantityState(SetProductQuantityStateRequest request) {
        ProductEntity product = findProduct(request.productId());
        product.setQuantityState(request.quantityState());
        return true;
    }

    @Transactional(readOnly = true)
    public ProductDto getProduct(UUID productId) {
        return productMapper.toDto(findProduct(productId));
    }

    private ProductEntity findProduct(UUID productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));
    }
}

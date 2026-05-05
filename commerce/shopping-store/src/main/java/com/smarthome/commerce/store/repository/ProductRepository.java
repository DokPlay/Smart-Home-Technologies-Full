package com.smarthome.commerce.store.repository;

import java.util.List;
import java.util.UUID;

import com.smarthome.commerce.api.store.ProductCategory;
import com.smarthome.commerce.api.store.ProductState;
import com.smarthome.commerce.store.model.ProductEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<ProductEntity, UUID> {

    List<ProductEntity> findByProductCategoryAndProductState(
            ProductCategory productCategory,
            ProductState productState,
            Pageable pageable
    );
}

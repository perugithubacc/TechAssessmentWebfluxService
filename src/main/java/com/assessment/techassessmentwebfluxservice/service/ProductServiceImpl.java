package com.assessment.techassessmentwebfluxservice.service;

import com.assessment.techassessmentwebfluxservice.Exceptions;
import com.assessment.techassessmentwebfluxservice.entity.ProductEntity;
import com.assessment.techassessmentwebfluxservice.repository.ProductRepository;
import com.techassessment.techassessmentwebfluxservice.model.Product;
import java.lang.reflect.Field;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ProductServiceImpl implements ProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductServiceImpl.class);
    private final ProductRepository productRepository;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @CacheEvict(value = "products", allEntries = true)
    @Override
    public Mono<Product> saveProduct(Mono<Product> productMono) {
        return productMono.flatMap(it -> {
            return new ProductEntity().toEntity(Mono.just(it))
                    .flatMap(entity -> productRepository.save(entity)
                            .flatMap(savedEntity -> {
                                it.setProductId(savedEntity.getProductId().toString());
                                log.info("Product info saved for ProductId: {}", it.getProductId());
                                return savedEntity.toDto();
                            })
                    );
        }).onErrorMap(DataIntegrityViolationException.class, ex -> {
            log.error("Error saving Product: {}", ex.getMessage());
            return new DataIntegrityViolationException(ex.getMessage());
        });
    }

    @CachePut(value = "products", key = "#productId")
    @Override
    public Mono<Product> updateProduct(String productId, Mono<Product> productMono) {
        log.info("Updating Product info for ProductId: {}", productId);

        return productRepository.findById(Long.valueOf(productId))
                .switchIfEmpty(Mono.error(
                                new Exceptions.ProductNotFoundException("Product Info not found for ProductId: " + productId)
                        )
                ).flatMap(existingProductInfo -> productMono
                        .flatMap(product -> new ProductEntity().toEntity(Mono.just(product))
                                .flatMap(newProductInfo -> {
                                    newProductInfo.setProductId(Long.valueOf(productId));
                                    if (existingProductInfo.equals(newProductInfo)) {
                                        return productRepository.save(newProductInfo)
                                                .flatMap(savedEntity -> {
                                                    log.info("Product info saved for ProductId: {}", product.getProductId());
                                                    return savedEntity.toDto();
                                                });
                                    } else {
                                        log.error("No changes are found for ProductId: {}", productId);
                                        return Mono.error(new Exceptions.NoChangeFoundException("No changes are found for ProductId: " + productId));
                                    }
                                })
                        )
                );
    }

    @CacheEvict(value = "products", allEntries = true)
    @Override
    public Mono<Void> removeProduct(String productId) {
        return productRepository.existsById(Long.valueOf(productId))
                .flatMap(exists -> {
                    if (!exists) {
                        log.error("Product Info not found for ProductId: " + productId);
                        return Mono.error(new Exceptions.ProductNotFoundException("Product Info not found for ProductId: " + productId));
                    } else {
                        return productRepository.deleteById(Long.valueOf(productId))
                                .doOnSuccess(unused -> log.info("Product Info deleted successfully for ProductId: {}", productId));
                    }
                });
    }

    @Cacheable(value = "products", key = "#productId")
    @Override
    public Mono<Product> getProductByProductCode(String productId) {
        return productRepository.findById(Long.valueOf(productId))
                .switchIfEmpty(Mono.error(new Exceptions.ProductNotFoundException(
                                "Product Info not found for ProductId: " + productId)
                        )
                )
                .flatMap(ProductEntity::toDto);
    }

    @Cacheable(value = "products", key = "#page + '-' + #size")
    @Override
    public Flux<Product> getProducts(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.findBy(pageable).flatMap(ProductEntity::toDto);
    }

    @Cacheable(value = "products", key = "#field + '-' + #value")
    @Override
    public Flux<Product> searchProduct(String field, String value) {
        return productRepository.findAll()
                .filter(product -> {
                    try {
                        Field declaredField = ProductEntity.class.getDeclaredField(field);
                        declaredField.setAccessible(true);
                        String fieldValue = (String) declaredField.get(product);
                        return value.equals(fieldValue);
                    } catch (NoSuchFieldException | IllegalAccessException e) {
                        throw new RuntimeException("Error accessing field: " + field, e);
                    }
                })
                .flatMap(ProductEntity::toDto);
    }
}

package com.assessment.techassessmentwebfluxservice.service;

import com.techassessment.techassessmentwebfluxservice.model.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductService {
    Mono<Product> saveProduct(Mono<Product> productMono);
    Mono<Product> updateProduct(String productId, Mono<Product> productMono);
    Mono<Void> removeProduct(String productId);

    Mono<Product> getProductByProductCode(String productId);
    Flux<Product> getProducts(
            Integer page,
            Integer size
    );
    Flux<Product> searchProduct(String field, String value);
}

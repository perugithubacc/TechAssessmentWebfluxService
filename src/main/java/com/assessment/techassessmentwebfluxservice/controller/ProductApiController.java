package com.assessment.techassessmentwebfluxservice.controller;

import com.assessment.techassessmentwebfluxservice.service.ProductService;
import com.techassessment.techassessmentwebfluxservice.api.ProductsApi;
import com.techassessment.techassessmentwebfluxservice.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/techassessmentwebfluxservice/api/v1/")
public class ProductApiController implements ProductsApi {
    private final ProductService productService;

    @Autowired
    public ProductApiController(ProductService productService) {
        this.productService = productService;
    }

    @Override
    public Mono<ResponseEntity<Product>> createProduct(Mono<Product> product, ServerWebExchange exchange) {
        return productService.saveProduct(product)
                .map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<Void>> deleteProductByProductCode(String productCode, ServerWebExchange exchange) {
        return productService.getProductByProductCode(productCode)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }

    @Override
    public Mono<ResponseEntity<Product>> getProductByProductCode(String productCode, ServerWebExchange exchange) {
        return productService.getProductByProductCode(productCode)
                .map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<Flux<Product>>> listProducts(Integer page, Integer size, ServerWebExchange exchange) {
        return Mono.just(ResponseEntity.ok(productService.getProducts(page, size)));
    }

    @Override
    public Mono<ResponseEntity<Flux<Product>>> searchProducts(String field, String value, ServerWebExchange exchange) {
        return Mono.just(ResponseEntity.ok(productService.searchProduct(field, value)));
    }

    @Override
    public Mono<ResponseEntity<Product>> updateProductByProductCode(String productCode, Mono<Product> product, ServerWebExchange exchange) {
        return productService.updateProduct(productCode, product)
                .map(ResponseEntity::ok);
    }
}

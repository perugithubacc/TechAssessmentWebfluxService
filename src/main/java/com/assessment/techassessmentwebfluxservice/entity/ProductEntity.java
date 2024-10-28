package com.assessment.techassessmentwebfluxservice.entity;

import com.techassessment.techassessmentwebfluxservice.model.Product;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import reactor.core.publisher.Mono;

@Table(name = "products")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
@ToString
@EqualsAndHashCode
public class ProductEntity {
    @Id
    @Column("product_id")
    private Long productId;

    @Column("product_code")
    private String productCode;

    @Column("product_name")
    private String productName;

    @Column("product_description")
    private String productDescription;

    @Column("product_price")
    private BigDecimal productPrice;

    @Column("product_quantity")
    private Integer productQuantity;

    @Column("product_type")
    private String productType;

    public Mono<Product> toDto() {
        return Mono.fromCallable(()-> Product.builder()
                .productCode(this.productCode)
                .productDescription(this.productDescription)
                .productId(String.valueOf(this.productId))
                .productName(this.productName)
                .productPrice(this.productPrice)
                .productQuantity(this.productQuantity)
                .productType(this.productType)
                .build());
    }

    public Mono<ProductEntity> toEntity(Mono<Product> productMono) {
       return productMono.map(product -> ProductEntity.builder()
               .productCode(product.getProductCode())
               .productDescription(product.getProductDescription())
               .productName(product.getProductName())
               .productPrice(product.getProductPrice())
               .productQuantity(product.getProductQuantity())
               .productType(product.getProductType())
               .build());
    }
}

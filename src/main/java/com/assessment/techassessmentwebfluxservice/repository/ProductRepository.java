package com.assessment.techassessmentwebfluxservice.repository;


import com.assessment.techassessmentwebfluxservice.entity.ProductEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ProductRepository extends R2dbcRepository<ProductEntity, Long>{
    Flux<ProductEntity> findBy(Pageable pageable);

}

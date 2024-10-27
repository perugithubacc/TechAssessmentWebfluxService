package com.assessment.techassessmentwebfluxservice.repository;

import com.assessment.techassessmentwebfluxservice.entity.CustomerEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface CustomerRepository extends R2dbcRepository<CustomerEntity, Long> {
    @Query("SELECT * FROM customer WHERE DATE_PART('year', AGE(CURRENT_DATE, birthday)) > :ageGreaterThan")
    Flux<CustomerEntity> findByAgeGreaterThan(@Param("ageGreaterThan") Integer ageGreaterThan, Pageable pageable);

    Flux<CustomerEntity> findBy(Pageable pageable);

}



package com.assessment.techassessmentwebfluxservice.service;

import com.assessment.techassessmentwebfluxservice.Exceptions;
import com.assessment.techassessmentwebfluxservice.entity.CustomerEntity;
import com.assessment.techassessmentwebfluxservice.repository.CustomerRepository;
import com.techassessment.techassessmentwebfluxservice.model.Customer;
import java.lang.reflect.Field;
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
public class CustomerServiceImpl implements CustomerService {

    private static final Logger log = LoggerFactory.getLogger(CustomerServiceImpl.class);

    private final CustomerRepository customerRepository;

    @Autowired
    public CustomerServiceImpl(
            CustomerRepository customerRepository
    ) {
        this.customerRepository = customerRepository;
    }

    @CacheEvict(value = "customers", allEntries = true)
    @Override
    public Mono<Customer> saveCustomer(Mono<Customer> customer) {
        return customer.flatMap(it -> new CustomerEntity().toEntity(Mono.just(it))
                .flatMap(entity -> customerRepository.save(entity)
                        .flatMap(savedEntity -> {
                            it.setCustomerId(savedEntity.getCustomerId().toString());
                            log.info("Customer info saved for customerId: {}", it.getCustomerId());
                            return savedEntity.toDto();
                        })
                )
        ).doOnError(ex -> log.error("Error saving customer: {}", ex.getMessage()))
                .onErrorMap(DataIntegrityViolationException.class, ex ->
                        new DataIntegrityViolationException("Duplicate key value violates unique constraint", ex));
    }

    @CachePut(value = "customers", key = "#customerId")
    @Override
    public Mono<Customer> updateCustomer(String customerId, Mono<Customer> customerMono) {
        log.info("Updating customer info for customerId: {}", customerId);

        return customerRepository.findById(Long.valueOf(customerId))
                .switchIfEmpty(Mono.error(
                        new Exceptions.CustomerNotFoundException("Customer Info not found for customerId: " + customerId)
                        )
                ).flatMap(existingCustomerInfo -> customerMono
                        .flatMap(customer -> new CustomerEntity().toEntity(Mono.just(customer))
                                .flatMap(newCustomerInfo -> {
                                    newCustomerInfo.setCustomerId(Long.valueOf(customerId));
                                    if (!existingCustomerInfo.equals(newCustomerInfo)) {
                                        return customerRepository.save(newCustomerInfo)
                                                .flatMap(savedEntity -> {
                                                    log.info("Customer info saved for customerId: {}", customer.getCustomerId());
                                                    return savedEntity.toDto();
                                                });
                                    } else {
                                        log.error("No changes are found for customerId: {}", customerId);
                                        return Mono.error(new Exceptions.NoChangeFoundException("No changes are found for customerId: " + customerId));
                                    }
                                })
                        )
                );
    }

    @CacheEvict(value = "customers", allEntries = true)
    @Override
    public Mono<Void> removeCustomer(String customerId) {
        return customerRepository.existsById(Long.valueOf(customerId))
                .flatMap(exists -> {
                    if (!exists) {
                        log.error("Customer Info not found for customerId: " + customerId);
                        return Mono.error(new Exceptions.CustomerNotFoundException("Customer Info not found for customerId: " + customerId));
                    } else {
                        return customerRepository.deleteById(Long.valueOf(customerId))
                                .doOnSuccess(unused -> log.info("Customer Info deleted successfully for customerId: {}", customerId));
                    }
                });
    }

    @Cacheable(value = "customers", key = "#customerId")
    @Override
    public Mono<Customer> getCustomerById(String customerId) {
        return customerRepository.findById(Long.valueOf(customerId))
                .switchIfEmpty(Mono.error(new Exceptions.CustomerNotFoundException(
                        "Customer Info not found for customerId: " + customerId)
                        )
                )
                .flatMap(CustomerEntity::toDto);
    }

    @Cacheable(value = "customers", key = "#page + '-' + #size + '-' + #ageGreaterThan")
    @Override
    public Flux<Customer> getCustomers(Integer page, Integer size, Integer ageGreaterThan) {
        Pageable pageable = PageRequest.of(page, size);

        Flux<CustomerEntity> customerEntities;

        if (ageGreaterThan != null && ageGreaterThan > 0) {
            customerEntities = customerRepository.findByAgeGreaterThan(ageGreaterThan, pageable);
        } else {
            customerEntities = customerRepository.findBy(pageable);
        }

        return customerEntities.flatMap(CustomerEntity::toDto);
    }

    @Cacheable(value = "customers", key = "#field + '-' + #value")
    @Override
    public Flux<Customer> searchCustomers(String field, String value) {
        return customerRepository.findAll()
                .filter(customer -> {
                    try {
                        Field declaredField = CustomerEntity.class.getDeclaredField(field);
                        declaredField.setAccessible(true);
                        String fieldValue = (String) declaredField.get(customer);
                        return value.equals(fieldValue);
                    } catch (NoSuchFieldException | IllegalAccessException e) {
                        throw new RuntimeException("Error accessing field: " + field, e);
                    }
                })
                .flatMap(CustomerEntity::toDto);
    }
}

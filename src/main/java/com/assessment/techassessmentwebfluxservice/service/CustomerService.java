package com.assessment.techassessmentwebfluxservice.service;

import com.techassessment.techassessmentwebfluxservice.model.Customer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CustomerService {
    Mono<Customer> saveCustomer(Mono<Customer> customer);
    Mono<Customer> updateCustomer(String customerId, Mono<Customer> customer);
    Mono<Void> removeCustomer(String customerId);

    Mono<Customer> getCustomerById(String customerId);
    Flux<Customer> getCustomers(
            Integer page,
            Integer size,
            Integer ageGreaterThan
    );
    Flux<Customer> searchCustomers(String field, String value);
}

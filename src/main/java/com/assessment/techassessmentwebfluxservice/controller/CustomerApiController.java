package com.assessment.techassessmentwebfluxservice.controller;


import com.assessment.techassessmentwebfluxservice.service.CustomerProducerService;
import com.assessment.techassessmentwebfluxservice.service.CustomerService;
import com.techassessment.techassessmentwebfluxservice.api.CustomersApi;
import com.techassessment.techassessmentwebfluxservice.model.Customer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/techassessmentwebfluxservice/api/v1/")
public class CustomerApiController implements CustomersApi {

    private final CustomerService customerService;
    private final CustomerProducerService customerProducerService;

    public CustomerApiController(CustomerService customerService, CustomerProducerService customerProducerService) {
        this.customerService = customerService;
        this.customerProducerService = customerProducerService;
    }

    @Override
    public Mono<ResponseEntity<Customer>> createCustomer(Mono<Customer> customer, ServerWebExchange exchange) {
        return customerService.saveCustomer(customer)
                .map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<Void>> deleteCustomer(String customerId, ServerWebExchange exchange) {
         return customerService.removeCustomer(customerId)
                 .then(Mono.just(ResponseEntity.noContent().build()));
    }

    @Override
    public Mono<ResponseEntity<Customer>> getCustomerById(String customerId, ServerWebExchange exchange) {
        return customerService.getCustomerById(customerId)
                .map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<Flux<Customer>>> getCustomers(Integer page, Integer size, Integer ageGreaterThan, ServerWebExchange exchange) {
        return Mono.just(ResponseEntity.ok(customerService.getCustomers(page, size, ageGreaterThan)));
    }

    @Override
    public Mono<ResponseEntity<Flux<Customer>>> searchCustomers(String field, String value, ServerWebExchange exchange) {
        return Mono.just(ResponseEntity.ok(customerService.searchCustomers(field, value)));
    }

    @Override
    public Mono<ResponseEntity<String>> submitNewCustomer(Mono<Customer> customerMono, ServerWebExchange exchange) {
        return customerMono
                .flatMap(customer -> {
                    customerProducerService.publish(customer);
                    return Mono.empty();
                })
                .then(Mono.just(ResponseEntity.ok("Customer creation submitted")));
    }

    @Override
    public Mono<ResponseEntity<Customer>> updateCustomer(String customerId, Mono<Customer> customer, ServerWebExchange exchange) {
        return customerService.updateCustomer(customerId, customer)
                .map(ResponseEntity::ok);
    }
}

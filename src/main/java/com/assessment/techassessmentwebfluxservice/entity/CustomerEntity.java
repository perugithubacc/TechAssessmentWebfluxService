package com.assessment.techassessmentwebfluxservice.entity;

import com.assessment.techassessmentwebfluxservice.model.CustomerRecord;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.techassessment.techassessmentwebfluxservice.model.Customer;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
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

@Table(name = "customer")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
@ToString
@EqualsAndHashCode
public class CustomerEntity {
    @Id
    @Column("customer_id")
    private Long customerId;

    @Column("document_id")
    private String documentId;

    @Column("first_name")
    private String firstName;

    @Column("middle_name")
    private String middleName;

    @Column("last_name")
    private String lastName;

    @Column("gender")
    private String gender;

    @Column("birthday")
    private LocalDate birthday;

    @Column("mobile_number")
    private String mobileNumber;

    @Column("office_number")
    private String officeNumber;

    @Column("personal_email")
    private String personalEmail;

    @Column("office_email")
    private String officeEmail;

    @Column("family_members")
    private String familyMembers;

    @Column("address")
    private String address;

    @Column("customer_type")
    private String customerType;

    @Column("age")
    private Integer age;

    public Mono<Customer> toDto() {
        return Mono.fromCallable(() -> {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                @SuppressWarnings("unchecked")
                List<String> family_members = objectMapper.readValue(this.familyMembers, List.class);
                @SuppressWarnings("unchecked")
                List<String> addressList = objectMapper.readValue(this.address, List.class);

                return Customer.builder()
                        .customerId(this.customerId.toString())
                        .documentId(this.documentId)
                        .firstName(this.firstName)
                        .middleName(this.middleName)
                        .lastName(this.lastName)
                        .birthday(this.birthday)
                        .gender(Customer.GenderEnum.valueOf(this.gender))
                        .customerType(this.customerType)
                        .age(Period.between(birthday, LocalDate.now()).getYears())
                        .mobileNumber(this.mobileNumber)
                        .officeNumber(this.officeNumber)
                        .officeEmail(this.officeEmail)
                        .personalEmail(this.personalEmail)
                        .documentId(this.documentId)
                        .familyMembers(family_members)
                        .address(addressList)
                        .build();
            } catch (JsonProcessingException ex) {
                throw new RuntimeException("Json processing exception occurred: " + this.getCustomerId());
            }
        });

    }

    public Mono<CustomerEntity> toEntity(Mono<Customer> customerMono) {
        return customerMono.map(customer -> {
            try {
                return CustomerEntity.builder()
                        .firstName(customer.getFirstName())
                        .middleName(customer.getMiddleName())
                        .lastName(customer.getLastName())
                        .documentId(customer.getDocumentId())
                        .birthday(customer.getBirthday())
                        .gender(customer.getGender().getValue())
                        .customerType(customer.getCustomerType())
                        .officeEmail(customer.getOfficeEmail())
                        .personalEmail(customer.getPersonalEmail())
                        .mobileNumber(customer.getMobileNumber())
                        .officeNumber(customer.getOfficeNumber())
                        .familyMembers(new ObjectMapper().writeValueAsString(customer.getFamilyMembers()))
                        .address(new ObjectMapper().writeValueAsString(customer.getAddress()))
                        .build();
            } catch (JsonProcessingException ex) {
                throw new RuntimeException("Json processing exception occurred: " + customer.getCustomerId());
            }
        });
    }

    public static CustomerRecord toRecord(Customer customer) {
        CustomerRecord customerRecord = new CustomerRecord();
        try {
            customerRecord.setFirstName(customer.getFirstName());
            customerRecord.setMiddleName(customer.getMiddleName());
            customerRecord.setLastName(customer.getLastName());
            customerRecord.setDocumentId(customer.getDocumentId());
            customerRecord.setBirthday(customer.getBirthday());
            customerRecord.setGender(customer.getGender().getValue());
            customerRecord.setMobileNumber(customer.getMobileNumber());
            customerRecord.setOfficeNumber(customer.getOfficeNumber());
            customerRecord.setOfficeEmail(customer.getOfficeEmail());
            customerRecord.setPersonalEmail(customer.getPersonalEmail());
            customerRecord.setCustomerType(customer.getCustomerType());
            customerRecord.setFamilyMembers(new ObjectMapper().writeValueAsString(customer.getFamilyMembers()));
            customerRecord.setAddress(new ObjectMapper().writeValueAsString(customer.getAddress()));
        } catch (JsonProcessingException ex) {
            throw new RuntimeException("Json processing exception occurred: " + customer.getCustomerId());
        }
        return customerRecord;
    }

    public static CustomerEntity toEntity(CustomerRecord customer) {
        try {
            return CustomerEntity.builder()
                    .firstName(customer.getFirstName())
                    .middleName(customer.getMiddleName())
                    .lastName(customer.getLastName())
                    .documentId(customer.getDocumentId())
                    .birthday(customer.getBirthday())
                    .gender(customer.getGender())
                    .customerType(customer.getCustomerType())
                    .officeEmail(customer.getOfficeEmail())
                    .personalEmail(customer.getPersonalEmail())
                    .mobileNumber(customer.getMobileNumber())
                    .officeNumber(customer.getOfficeNumber())
                    .familyMembers(new ObjectMapper().writeValueAsString(customer.getFamilyMembers()))
                    .address(new ObjectMapper().writeValueAsString(customer.getAddress()))
                    .build();
        } catch (JsonProcessingException ex) {
            throw new RuntimeException("Json processing exception occurred: " + customer.getCustomerId());
        }
    }
}

package com.assessment.techassessmentwebfluxservice;

import org.springframework.boot.SpringApplication;

public class TestTechAssessmentWebfluxServiceApplication {

    public static void main(String[] args) {
        SpringApplication.from(TechAssessmentWebfluxServiceApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}

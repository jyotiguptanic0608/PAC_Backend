package com.proposal.serviceimpl;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.core.annotation.Order;
import com.proposal.service.RoleService;

@Component
@Order(1)
public class RoleInitializer implements CommandLineRunner {

    private final RoleService service;

    public RoleInitializer(RoleService service) {
        this.service = service;
    }

    @Override
    public void run(String... args) {

        service.initializeRoles();

    }
}
package com.proposal.serviceimpl;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.proposal.entity.Employee;
import com.proposal.entity.Role;
import com.proposal.repository.EmployeeRepository;
import com.proposal.repository.RoleRepository;
import org.springframework.core.annotation.Order;

@Component
@Order(2)
public class AdminInitializer implements CommandLineRunner {

    private final EmployeeRepository employeeRepo;
    private final RoleRepository roleRepo;
    private final BCryptPasswordEncoder encoder;

    public AdminInitializer(EmployeeRepository employeeRepo,
                            RoleRepository roleRepo,
                            BCryptPasswordEncoder encoder) {

        this.employeeRepo = employeeRepo;
        this.roleRepo = roleRepo;
        this.encoder = encoder;
    }

    @Override
    public void run(String... args) {

        if(employeeRepo.findByUsername("ADMIN") == null) {

            Role adminRole =
                    roleRepo.findByRoleName("ADMIN");

            Employee admin = new Employee();

            admin.setName("System Administrator");

            admin.setEmail("admin@nic.in");

            admin.setContactNumber("9999999999");

            admin.setDepartmentName("Administration");

            admin.setUsername("ADMIN");

            admin.setPassword(
                    encoder.encode("admin123")
            );

            admin.setRole(adminRole);

            admin.setPacCommitteeMember(false);

            employeeRepo.save(admin);

            System.out.println("Admin Created");
        }
    }
}
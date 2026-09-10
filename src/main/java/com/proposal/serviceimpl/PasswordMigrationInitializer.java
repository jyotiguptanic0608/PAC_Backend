package com.proposal.serviceimpl;

import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.proposal.entity.Employee;
import com.proposal.repository.EmployeeRepository;
import com.proposal.repository.PacMemberRepository;

@Component
@Order(3)
public class PasswordMigrationInitializer implements CommandLineRunner {

    private final EmployeeRepository employeeRepo;
    private final PacMemberRepository pacMemberRepo;
    private final BCryptPasswordEncoder encoder;

    public PasswordMigrationInitializer(EmployeeRepository employeeRepo,
                                         PacMemberRepository pacMemberRepo,
                                         BCryptPasswordEncoder encoder) {
        this.employeeRepo = employeeRepo;
        this.pacMemberRepo = pacMemberRepo;
        this.encoder = encoder;
    }

    @Override
    public void run(String... args) {
        List<Employee> employees = employeeRepo.findAll();
        for (Employee employee : employees) {
            String pwd = employee.getPassword();
            if (pwd != null && !isBCrypt(pwd)) {
                String encodedPwd = encoder.encode(pwd);
                employee.setPassword(encodedPwd);
                employeeRepo.save(employee);

                pacMemberRepo.findByEmployeeId(employee.getId()).ifPresent(pac -> {
                    pac.setPassword(encodedPwd);
                    pacMemberRepo.save(pac);
                });
                System.out.println("Migrated plaintext password to BCrypt hash for user: " + employee.getUsername());
            }
        }
    }

    private boolean isBCrypt(String password) {
        return password.startsWith("$2a$") || password.startsWith("$2b$") || password.startsWith("$2y$");
    }
}

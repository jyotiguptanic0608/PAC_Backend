package com.proposal.controller;
import java.util.List;
import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.*;

import com.proposal.entity.Employee;
import com.proposal.service.EmployeeService;
import com.proposal.dto.RegisterResponse;

import com.proposal.service.SessionRegistryService;

@RestController
@RequestMapping("/api/employees")
@CrossOrigin(
    origins = "http://localhost:5173",
    allowCredentials = "true"
)
public class EmployeeController {

   private final EmployeeService service;
private final SessionRegistryService registry;

public EmployeeController(
        EmployeeService service,
        SessionRegistryService registry) {

    this.service = service;
    this.registry = registry;
}

    @PostMapping("/register")
    public RegisterResponse register(
            @RequestBody Employee employee) {

        return service.register(employee);

    }
  @PostMapping("/login")
public Employee login(
        @RequestParam String username,
        @RequestParam String password,
        @RequestParam String captcha,
        HttpSession session) {

    Employee employee =
            service.login(
                    username,
                    password,
                    captcha,
                    session
            );

    if (employee == null) {
        return null;
    }

  HttpSession oldSession = registry.getSession(username);

if (oldSession != null) {

    try {
        oldSession.invalidate();
    } catch (IllegalStateException e) {
        // session already invalid
    }
}

session.setAttribute("user", employee);

registry.addSession(username, session);

return employee;
}
@GetMapping("/validate-session")
public boolean validateSession(HttpSession session) {

    return session.getAttribute("user") != null;

}

@GetMapping("/{id}")
public Employee getEmployee(
        @PathVariable Long id){

    return service.getEmployee(id);

}

@PostMapping("/logout")
public void logout(HttpSession session) {

    Employee employee =
            (Employee) session.getAttribute("user");

    if (employee != null) {
        registry.removeSession(
                employee.getUsername()
        );
    }

    session.invalidate();
}
    
    @GetMapping("/pac-members")
    public List<Employee> getPacMembers() {
        return service.getPacMembers();
    }
    
    @GetMapping
    public List<Employee> getAllEmployees() {

        return service.getAllEmployees();
    }

}
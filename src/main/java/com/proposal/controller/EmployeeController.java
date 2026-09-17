package com.proposal.controller;
import java.util.List;
import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.*;

import com.proposal.entity.Employee;
import com.proposal.service.EmployeeService;
import com.proposal.dto.RegisterResponse;

import com.proposal.service.SessionRegistryService;

import com.proposal.dto.ForgotPasswordRequest;
import com.proposal.dto.ResetPasswordRequest;
import java.util.Map;

import com.proposal.dto.LoginRequest;

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

    @PostMapping({"/login", "/force-login"})
    public Employee login(
        @RequestBody(required = false) LoginRequest loginRequest,
        @RequestParam(required = false) String username,
        @RequestParam(required = false) String password,
        @RequestParam(required = false) String captcha,
        HttpSession session) {

    String user = (loginRequest != null && loginRequest.getUsername() != null)
            ? loginRequest.getUsername() : username;
    String pwd = (loginRequest != null && loginRequest.getPassword() != null)
            ? loginRequest.getPassword() : password;
    String cap = (loginRequest != null && loginRequest.getCaptcha() != null)
            ? loginRequest.getCaptcha() : captcha;

    Employee employee = service.login(user, pwd, cap, session);

    if (employee == null) {
        return null;
    }

    HttpSession oldSession = registry.getSession(user);

    if (oldSession != null) {
        try {
            oldSession.invalidate();
        } catch (IllegalStateException e) {
            // session already invalid
        }
    }

    session.setAttribute("user", employee);
    registry.addSession(user, session);

    return employee;
}

    @PostMapping("/forgot-password/send-otp")
    public Map<String, Object> sendForgotPasswordOtp(@RequestBody ForgotPasswordRequest request) {
        return service.sendForgotPasswordOtp(request.getIdentifier());
    }

    @PostMapping("/forgot-password/reset-password")
    public Map<String, Object> resetPasswordWithOtp(@RequestBody ResetPasswordRequest request) {
        return service.resetPasswordWithOtp(request);
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
    
    @GetMapping("/group-heads")
    public List<Employee> getGroupHeads() {
        return service.getGroupHeads();
    }
    
    @GetMapping
    public List<Employee> getAllEmployees() {

        return service.getAllEmployees();
    }

}
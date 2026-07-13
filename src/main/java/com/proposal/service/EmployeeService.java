package com.proposal.service;

import com.proposal.dto.RegisterResponse;
import java.util.List;

import com.proposal.entity.Employee;
import jakarta.servlet.http.HttpSession;


public interface EmployeeService {

    RegisterResponse register(Employee employee);

    Employee login(String username,
               String password,
               String captcha,
               HttpSession session);
    Employee getEmployee(Long id);
    
    List<Employee> getPacMembers();
    List<Employee> getAllEmployees();

}
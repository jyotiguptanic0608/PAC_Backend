package com.proposal.serviceimpl;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.util.List;
import org.springframework.stereotype.Service;

import com.proposal.dto.RegisterResponse;
import com.proposal.entity.Employee;
import com.proposal.repository.EmployeeRepository;
import com.proposal.service.EmployeeService;
import com.proposal.repository.RoleRepository;
import com.proposal.repository.PacMemberRepository;
import com.proposal.entity.Role;
import com.proposal.entity.PacMember;
import jakarta.servlet.http.HttpSession;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository repo;
    private final BCryptPasswordEncoder encoder;
    private final RoleRepository roleRepo;
private final PacMemberRepository pacMemberRepo;

    public EmployeeServiceImpl(EmployeeRepository repo,
                               BCryptPasswordEncoder encoder,RoleRepository roleRepo,PacMemberRepository pacMemberRepo) {
        this.repo = repo;
        this.encoder = encoder;
        this.roleRepo = roleRepo;
    this.pacMemberRepo = pacMemberRepo;
    }

 @Override
public RegisterResponse register(Employee employee) {

    long count = repo.count() + 1;

    String number = String.format("%03d", count);

    String username = "NIC@PAC" + number;
    String rawPassword = "nic@" + number;

    employee.setUsername(username);
    employee.setPassword(encoder.encode(rawPassword));

    if(employee.isPacCommitteeMember()) {

        Role pacRole =
                roleRepo.findByRoleName("PAC_MEMBER");

        employee.setRole(pacRole);
    }
    else {

        Role empRole =
                roleRepo.findByRoleName("EMPLOYEE");

        employee.setRole(empRole);
    }

    Employee savedEmployee = repo.save(savedEmployee);

    if(savedEmployee.isPacCommitteeMember()) {

      PacMember pacMember = new PacMember();

pacMember.setEmployee(employee);

pacMember.setName(employee.getName());
pacMember.setEmail(employee.getEmail());
pacMember.setContactNumber(employee.getContactNumber());
pacMember.setDepartmentName(employee.getDepartmentName());

pacMember.setDesignation(employee.getDesignation());
pacMember.setDivision(employee.getDivision());
pacMember.setStateOfPosting(employee.getStateOfPosting());
pacMember.setPermanentAddress(employee.getPermanentAddress());
pacMember.setIpNumber(employee.getIpNumber());
pacMember.setEmergencyContact(employee.getEmergencyContact());

pacMember.setUsername(employee.getUsername());
pacMember.setPassword(employee.getPassword());

pacMember.setPacDesignation("Member");

pacMemberRepo.save(pacMember);
    }

    return new RegisterResponse(username, rawPassword);
}@Override
public Employee login(String username,
                      String password,
                      String captcha,
                      HttpSession session)  {

   Employee employee = repo.findByUsername(username);

String actualCaptcha =
        (String) session.getAttribute("captcha");

if (actualCaptcha == null ||
    !actualCaptcha.equalsIgnoreCase(captcha)) {
    throw new RuntimeException("Invalid CAPTCHA");
}

if (employee == null) {
    return null;
}

if (encoder.matches(password, employee.getPassword())) {
    return employee;
}

return null;
}
    @Override
    public List<Employee> getAllEmployees() {

        return repo.findAll();
    }
    @Override
    public List<Employee> getPacMembers() {
        return repo.findByPacCommitteeMember(true);
    }

    @Override
public Employee getEmployee(Long id){

    return repo.findById(id).orElse(null);

}
}
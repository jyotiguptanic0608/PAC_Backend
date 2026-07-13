package com.proposal.serviceimpl;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

import com.proposal.entity.Employee;
import com.proposal.entity.Role;
import com.proposal.repository.EmployeeRepository;
import com.proposal.repository.RoleRepository;
import com.proposal.service.AdminService;
import com.proposal.entity.PacMember;
import com.proposal.repository.PacMemberRepository;


@Service
public class AdminServiceImpl implements AdminService {

    private final EmployeeRepository employeeRepo;
    private final PacMemberRepository pacMemberRepo;
    private final RoleRepository roleRepo;
public AdminServiceImpl(
        EmployeeRepository employeeRepo,
        RoleRepository roleRepo,
        PacMemberRepository pacMemberRepo) {

    this.employeeRepo = employeeRepo;
    this.roleRepo = roleRepo;
    this.pacMemberRepo = pacMemberRepo;
}

  @Override
public List<PacMember> getPacMembers() {

    return pacMemberRepo.findAll()
            .stream()
            .filter(pac ->
                    !pac.getEmployee()
                        .getRole()
                        .getRoleName()
                        .equals("ADMIN"))
            .toList();

}
   @Override
public void updatePacMembers(List<Long> ids){

    List<Employee> employees =
            employeeRepo.findAll();

    Role pacRole =
            roleRepo.findByRoleName("PAC_MEMBER");

    Role employeeRole =
            roleRepo.findByRoleName("EMPLOYEE");

  for(Employee employee : employees){
    if(employee.getRole().getRoleName().equals("ADMIN")){
        continue;
    }
    if(ids.contains(employee.getId())){
        employee.setPacCommitteeMember(true);
        employee.setRole(pacRole);
        if(
            pacMemberRepo
            .findByEmployeeId(employee.getId())
            .isEmpty()
        ){
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
    }
    else{
        employee.setPacCommitteeMember(false);
        employee.setRole(employeeRole);
        pacMemberRepo
            .findByEmployeeId(employee.getId())
            .ifPresent(pacMemberRepo::delete);
    }
}
    employeeRepo.saveAll(employees);
}

@Override
public Employee removePacMember(Long employeeId) {

    Employee employee =
            employeeRepo.findById(employeeId)
                        .orElseThrow();

    employee.setPacCommitteeMember(false);
    employee.setChairman(false);

    Role role = roleRepo.findByRoleName("EMPLOYEE");

    employee.setRole(role);

    pacMemberRepo
            .findByEmployeeId(employeeId)
            .ifPresent(pacMemberRepo::delete);

    return employeeRepo.save(employee);
}
@Override
public void makeChairman(Long employeeId) {

    Employee employee = employeeRepo.findById(employeeId)
            .orElseThrow();

    if (!employee.isPacCommitteeMember()) {
        throw new RuntimeException("Only PAC members can be chairman.");
    }

employeeRepo.findByChairmanTrue().ifPresent(old -> {

    if (!old.getId().equals(employeeId)) {

        old.setChairman(false);
        employeeRepo.save(old);

        pacMemberRepo.findByEmployeeId(old.getId())
                .ifPresent(pac -> {

                    pac.setPacDesignation("Member");

                    pacMemberRepo.save(pac);

                });

    }

});
    employee.setChairman(true);
    employeeRepo.save(employee);
}

@Override
public Employee removeChairman(Long employeeId) {

    Employee employee =
            employeeRepo.findById(employeeId)
                        .orElseThrow();

    employee.setChairman(false);

    return employeeRepo.save(employee);

}
}
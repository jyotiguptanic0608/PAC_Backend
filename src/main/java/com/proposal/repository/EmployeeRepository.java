package com.proposal.repository;

import java.util.List;

import com.proposal.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface EmployeeRepository
        extends JpaRepository<Employee, Long> {

    Employee findByUsername(String username);
    Employee findById(long id);
    Optional<Employee> findByChairmanTrue();

    List<Employee> findByPacCommitteeMember(boolean pacCommitteeMember);

    List<Employee> findByPacCommitteeMemberTrue();
}
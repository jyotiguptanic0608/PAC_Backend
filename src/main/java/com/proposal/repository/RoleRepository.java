package com.proposal.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proposal.entity.Role;

public interface RoleRepository extends JpaRepository<Role,Long>{

    Role findByRoleName(String roleName);

}
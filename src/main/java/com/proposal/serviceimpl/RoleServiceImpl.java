package com.proposal.serviceimpl;

import org.springframework.stereotype.Service;

import com.proposal.entity.Role;
import com.proposal.repository.RoleRepository;
import com.proposal.service.RoleService;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository repo;

    public RoleServiceImpl(RoleRepository repo) {
        this.repo = repo;
    }

    @Override
    public void initializeRoles() {

        if(repo.findByRoleName("EMPLOYEE")==null){

            Role role = new Role();

            role.setRoleName("EMPLOYEE");
            role.setRoleDescription("Normal Employee");
            role.setRoleAbbreviation("EMP");

            repo.save(role);
        }

        if(repo.findByRoleName("PAC_MEMBER")==null){

            Role role = new Role();

            role.setRoleName("PAC_MEMBER");
            role.setRoleDescription("PAC Committee Member");
            role.setRoleAbbreviation("PAC");

            repo.save(role);
        }

        if(repo.findByRoleName("CHAIRMAN")==null){

            Role role = new Role();

            role.setRoleName("CHAIRMAN");
            role.setRoleDescription("Head of the commitee");
            role.setRoleAbbreviation("CHM");

            repo.save(role);
        }

        if(repo.findByRoleName("ADMIN")==null){

            Role role = new Role();

            role.setRoleName("ADMIN");
            role.setRoleDescription("System Administrator");
            role.setRoleAbbreviation("ADM");

            repo.save(role);
        }

    }
}
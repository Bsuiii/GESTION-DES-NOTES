package com.example.school_managment_system.repositories;

import com.example.school_managment_system.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role,Integer> {
     Optional<Role> findByCode(String code);
}

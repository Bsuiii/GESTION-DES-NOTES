package com.example.school_managment_system.repositories;

import com.example.school_managment_system.models.Module;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ModuleRepository extends JpaRepository<Module, Integer> {
    // To check if a module with the same code already exists
    Optional<Module> findByCode(String code);
}
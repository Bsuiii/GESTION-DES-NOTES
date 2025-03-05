package com.example.school_managment_system.repositories;

import com.example.school_managment_system.models.Module;
import com.example.school_managment_system.models.Niveau;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ModuleRepository extends JpaRepository<Module, Integer> {
    // To check if a module with the same code already exists
    Optional<Module> findByCode(String code);

    List<Module> findByNiveau(Niveau niveau);
}
package com.example.school_managment_system.repositories;

import com.example.school_managment_system.models.EnseignantModule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EnseignantModuleRepository extends JpaRepository<EnseignantModule,Integer> {
}

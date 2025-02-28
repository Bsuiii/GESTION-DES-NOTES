package com.example.school_managment_system.repositories;

import com.example.school_managment_system.models.Enseignant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EnseignantRepository extends JpaRepository<Enseignant,Integer> {

    Optional<Enseignant> findByEmail(String email);
}

package com.example.school_managment_system.repositories;

import com.example.school_managment_system.models.Filiere;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FiliereRepository extends JpaRepository<Filiere,Integer> {

    Optional<Filiere> findFiliereByAlias(String alias);
    Optional<Filiere> findByAlias(String alias);
}

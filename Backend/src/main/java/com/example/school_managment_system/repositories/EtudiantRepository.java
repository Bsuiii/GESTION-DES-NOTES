package com.example.school_managment_system.repositories;

import com.example.school_managment_system.models.Etudiant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EtudiantRepository extends JpaRepository<Etudiant,Integer> {

    Optional<Etudiant> findByCne(String cne);

    @Query("SELECT MAX(e.id) FROM Etudiant e")
    Integer findMaxId();

}

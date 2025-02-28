package com.example.school_managment_system.repositories;

import com.example.school_managment_system.models.Niveau;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NiveauRepository extends JpaRepository<Niveau,Integer> {

    Optional<Niveau> findByAlias(String alias);

    @Query("SELECT n.niveauSuivant.id FROM Niveau n WHERE n.id = :niveauId")
    Optional<Integer> findNiveauSuivantId(@Param("niveauId") int niveauId);

}

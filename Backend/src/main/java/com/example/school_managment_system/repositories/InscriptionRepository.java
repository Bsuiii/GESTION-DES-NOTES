package com.example.school_managment_system.repositories;

import com.example.school_managment_system.models.Etudiant;
import com.example.school_managment_system.models.Inscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InscriptionRepository extends JpaRepository<Inscription,Integer> {

    Etudiant findByEtudiant_Id(int etudiant_id);
    @Query("SELECT i.niveau.id FROM Inscription i WHERE i.etudiant.id = :etudiantId ORDER BY i.id DESC")
    Optional<Integer> findLastNiveauIdByEtudiantId(@Param("etudiantId") int etudiantId);
}

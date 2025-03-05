package com.example.school_managment_system.repositories;

import com.example.school_managment_system.models.Etudiant;
import com.example.school_managment_system.models.Module;
import com.example.school_managment_system.models.Note;
import com.example.school_managment_system.utils.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface NoteRepository extends JpaRepository<Note,Integer> {

    List<Note> findByEtudiantAndModuleAndSession(Etudiant etudiant, Module module, Session session);


    @Query("""
            SELECT
                SUM(n.note)
            FROM Note n
            JOIN Element e ON n.element.id = e.id
            JOIN Module m ON e.module.id = m.id
            WHERE n.etudiant.id = :studentId
            AND m.id = :moduleId
            AND n.session = :session
            """)
    Optional<BigDecimal> findModuleAverage(
            @Param("studentId") int studentId,
            @Param("moduleId") int moduleId,
            @Param("session") Session session);

    @Query("SELECT n FROM Note n WHERE n.etudiant.id = :studentId AND n.element.id = :elementId AND n.session = :session")
    Optional<Note> findByEtudiantAndElementAndSession(
            @Param("studentId") int studentId,
            @Param("elementId") int elementId,
            @Param("session") Session session);


    Optional<Note> findByEtudiantIdAndElementIdAndSession(int etudiantu_id,int element_id,Session session);
}

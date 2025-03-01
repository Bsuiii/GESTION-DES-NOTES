package com.example.school_managment_system.repositories;

import com.example.school_managment_system.models.Etudiant;
import com.example.school_managment_system.models.Module;
import com.example.school_managment_system.models.Note;
import com.example.school_managment_system.utils.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoteRepository extends JpaRepository<Note,Integer> {

    List<Note> findByEtudiantAndModuleAndSession(Etudiant etudiant, Module module, Session session);

}

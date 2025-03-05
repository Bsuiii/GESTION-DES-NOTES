package com.example.school_managment_system.repositories;

import com.example.school_managment_system.models.Element;
import com.example.school_managment_system.models.Etudiant;
import com.example.school_managment_system.models.Module;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ElementRepository extends JpaRepository<Element,Integer> {

    Optional<Element> findByCode(String code);

    List<Element> findByModule(Module module);

    Optional<Element> findByTitreAndModuleId(String titre, int moduleId);
}

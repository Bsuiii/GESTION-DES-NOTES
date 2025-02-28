package com.example.school_managment_system.repositories;

import com.example.school_managment_system.models.Element;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ElementRepository extends JpaRepository<Element,Integer> {

    Optional<Element> findByCode(String code);
}

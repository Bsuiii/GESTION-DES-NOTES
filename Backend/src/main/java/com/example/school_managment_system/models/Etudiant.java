package com.example.school_managment_system.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "etudiant")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Etudiant {

    @Id
    private int id;
    private String nom;
    private String prenom;

    @Column(unique = true)
    private String cne;

    @OneToMany(mappedBy = "etudiant", cascade = CascadeType.ALL, fetch = FetchType.LAZY) // Fixed mappedBy
    private List<Inscription> inscriptions;

    @OneToMany(mappedBy = "etudiant", cascade = CascadeType.ALL, fetch = FetchType.LAZY) // Fixed mappedBy
    private List<Note> notes;
}

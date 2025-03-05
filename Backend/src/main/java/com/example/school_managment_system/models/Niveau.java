package com.example.school_managment_system.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "niveau")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Niveau {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String niveau;
    private String alias;

    @ManyToOne
    @JsonIgnore
    private Filiere filiere;

    @ManyToOne
    @JoinColumn(name = "id_niveau_suivant")
    private Niveau niveauSuivant;

    @OneToMany(mappedBy = "niveau", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Module> modules;

    @OneToMany(mappedBy = "niveau", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Inscription> inscriptions;
}

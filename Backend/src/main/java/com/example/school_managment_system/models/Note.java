package com.example.school_managment_system.models;

import com.example.school_managment_system.models.Element;
import com.example.school_managment_system.models.Etudiant;
import com.example.school_managment_system.models.Module;
import com.example.school_managment_system.utils.Session;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "notes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "etudiant_id", nullable = false)
    private Etudiant etudiant;

    @ManyToOne
    @JoinColumn(name = "module_id", nullable = false)
    private Module module;

    @ManyToOne
    @JoinColumn(name = "element_id", nullable = false)
    private Element element;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Session session; // NORMALE or RATTRAPAGE

    @Column(precision = 5, scale = 2, nullable = false)
    private BigDecimal note;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

}

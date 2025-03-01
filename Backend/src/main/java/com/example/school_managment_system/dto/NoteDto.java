package com.example.school_managment_system.dto;

import com.example.school_managment_system.models.Note;
import com.example.school_managment_system.utils.Session;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoteDto {

    private int id;

    private int etudiantId;
    private int moduleId;
    private int elementId;

    private Session session;

    @DecimalMin(value = "0.0", message = "Note must be greater than or equal to 0.0")
    @DecimalMax(value = "20.0", message = "Note must be less than or equal to 20.0")
    private BigDecimal note;

    // Static method to convert Note entity to NoteDto
    public static NoteDto toDto(Note note) {
        return NoteDto.builder()
                .id(note.getId())
                .etudiantId(note.getEtudiant().getId())
                .moduleId(note.getModule().getId())
                .elementId(note.getElement().getId())
                .session(note.getSession())
                .note(note.getNote())
                .build();
    }
}
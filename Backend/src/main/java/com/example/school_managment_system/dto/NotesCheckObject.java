package com.example.school_managment_system.dto;

import com.example.school_managment_system.models.Etudiant;
import lombok.*;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class NotesCheckObject {

    private int id;
    private NoteDto current_info;
    private NoteDto new_info;
}

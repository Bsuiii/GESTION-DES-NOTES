package com.example.school_managment_system.dto;

import com.example.school_managment_system.models.EnseignantModule;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnseignantModuleDto {
    private int id;
    private Date start_date;
    private Date end_date;
    private int module_id; // ID of the associated module
    private String module_titre; // Title of the associated module
    private String module_code; // Code of the associated module
    private int enseignant_id; // ID of the associated enseignant
    private String enseignant_nom; // Name of the associated enseignant
    private String enseignant_prenom; // First name of the associated enseignant

    public static EnseignantModuleDto toDto(EnseignantModule enseignantModule) {
        return EnseignantModuleDto.builder()
                .id(enseignantModule.getId())
                .start_date(enseignantModule.getStart_date())
                .end_date(enseignantModule.getEnd_date())
                .module_id(enseignantModule.getModule().getId())
                .module_titre(enseignantModule.getModule().getTitre())
                .module_code(enseignantModule.getModule().getCode())
                .enseignant_id(enseignantModule.getEnseignant().getId())
                .enseignant_nom(enseignantModule.getEnseignant().getNom())
                .enseignant_prenom(enseignantModule.getEnseignant().getPrenom())
                .build();
    }
}
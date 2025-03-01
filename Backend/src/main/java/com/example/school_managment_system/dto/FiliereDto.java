package com.example.school_managment_system.dto;

import com.example.school_managment_system.models.Filiere;
import lombok.Data;

@Data
public class FiliereDto {
    private int id;
    private String alias;
    private String intitule;
    private int anneeAccreditation;
    private int anneeFinAccreditation;
    private int coordonnateurId;
    private String coordonnateur_nomComplet;
    private int X,Y;
    public static FiliereDto toDto(Filiere filiere) {
        if (filiere == null) {
            return null;
        }
        FiliereDto dto = new FiliereDto();
        dto.setId(filiere.getId());
        dto.setAlias(filiere.getAlias());
        dto.setIntitule(filiere.getIntitule());
        dto.setAnneeAccreditation(filiere.getAnneeAccreditation());
        dto.setAnneeFinAccreditation(filiere.getAnneeFinAccreditation());
        dto.setCoordonnateurId(filiere.getCoordonnateur().getId());
        dto.setX(filiere.getX());
        dto.setY(filiere.getY());
        dto.setCoordonnateur_nomComplet(filiere.getCoordonnateur().getNom()+" "+filiere.getCoordonnateur().getPrenom());
        return dto;
    }

}

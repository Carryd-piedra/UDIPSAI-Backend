package com.ucacue.udipsai.modules.psicologiaeducativa.components;

import com.ucacue.udipsai.modules.psicologiaeducativa.PsicologiaEducativa.*;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Lob;
import lombok.Data;

@Embeddable
@Data
public class EstadoGeneral {

    @Enumerated(EnumType.STRING)
    @Column(name = "aprovechamiento_general")
    private AprovechamientoGeneral aprovechamientoGeneral;

    @Lob
    @Column(name = "actividad_escolar", columnDefinition = "TEXT")
    private String actividadEscolar;

    @Lob
    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;
}

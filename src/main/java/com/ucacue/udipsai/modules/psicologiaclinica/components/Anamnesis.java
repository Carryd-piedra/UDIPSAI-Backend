package com.ucacue.udipsai.modules.psicologiaclinica.components;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Lob;
import lombok.Data;

@Embeddable
@Data
public class Anamnesis {

    @Lob
    @Column(name = "anamnesis_familiar", columnDefinition = "TEXT")
    private String anamnesisFamiliar;

    @Lob
    @Column(name = "personal", columnDefinition = "TEXT")
    private String personal;

    @Lob
    @Column(name = "momentos_evolutivos_en_el_desarrollo", columnDefinition = "TEXT")
    private String momentosEvolutivosEnElDesarrollo;

    @Lob
    @Column(name = "habitos_en_la_oralidad", columnDefinition = "TEXT")
    private String habitosEnLaOralidad;
}

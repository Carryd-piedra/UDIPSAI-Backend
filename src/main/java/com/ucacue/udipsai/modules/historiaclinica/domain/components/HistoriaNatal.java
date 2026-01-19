package com.ucacue.udipsai.modules.historiaclinica.domain.components;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class HistoriaNatal {

    @Column(name = "parto")
    private String parto;

    @Column(name = "llanto_al_nacer")
    private String llantoAlNacer;

    @Column(name = "color_piel_nacimiento")
    private String colorPielNacimiento;

    @Column(name = "cordon_ombilical")
    private String cordonOmbilical;

    @Column(name = "presencia_ictericia")
    private Boolean presenciaIctericia;

    @Column(name = "transfucion_sangre")
    private Boolean transfucionSangre;
}

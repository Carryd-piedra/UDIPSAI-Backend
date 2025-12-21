package com.ucacue.udipsai.modules.fichamedica.components;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Lob;
import lombok.Data;

@Embeddable
@Data
public class AntecedentesMedicos {

    @Lob
    @Column(name = "alergias", columnDefinition = "TEXT")
    private String alergias;

    @Lob
    @Column(name = "enfermedades_virales", columnDefinition = "TEXT")
    private String enfermedadesVirales;

    @Lob
    @Column(name = "hospitalizaciones_quirurgicas_y_causas", columnDefinition = "TEXT")
    private String hospitalizacionesQuirurgicasYCausas;

    @Lob
    @Column(name = "accidentes_y_secuelas", columnDefinition = "TEXT")
    private String accidentesYSecuelas;

    @Lob
    @Column(name = "toma_medicacion_actualmente", columnDefinition = "TEXT")
    private String tomaMedicacionActualmente;

    @Lob
    @Column(name = "examenes_complementarios_realizados", columnDefinition = "TEXT")
    private String examenesComplementariosRealizados;

    @Lob
    @Column(name = "antecedentes_patologicos_familiares", columnDefinition = "TEXT")
    private String antecedentesPatologicosFamiliares;

    @Lob
    @Column(name = "vacunacion_c", columnDefinition = "TEXT")
    private String vacunacionC;
}

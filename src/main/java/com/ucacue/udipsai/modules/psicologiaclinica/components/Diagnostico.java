package com.ucacue.udipsai.modules.psicologiaclinica.components;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Lob;
import lombok.Data;

@Embeddable
@Data
public class Diagnostico {

    @Lob
    @Column(name = "impresion_diagnostica", columnDefinition = "TEXT")
    private String impresionDiagnostica;

    @Lob
    @Column(name = "derivacion_interconsulta", columnDefinition = "TEXT")
    private String derivacionInterconsulta;

    @Lob
    @Column(name = "objetivo_plan_tratamiento_individual", columnDefinition = "TEXT")
    private String objetivoPlanTratamientoIndividual;

    @Lob
    @Column(name = "estrategia_de_intervencion", columnDefinition = "TEXT")
    private String estrategiaDeIntervencion;

    @Lob
    @Column(name = "indicador_de_logro", columnDefinition = "TEXT")
    private String indicadorDeLogro;

    @Column(name = "tiempo_estimado")
    private String tiempoEstimado;

    @Lob
    @Column(name = "evaluacion", columnDefinition = "TEXT")
    private String evaluacion;
}

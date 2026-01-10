package com.ucacue.udipsai.modules.psicologiaeducativa;

import com.ucacue.udipsai.modules.psicologiaeducativa.components.*;
import lombok.Data;

@Data
public class PsicologiaEducativaRequest {
    private Integer pacienteId;
    private Boolean activo;
    private HistoriaEscolar historiaEscolar;
    private Desarrollo desarrollo;
    private Adaptacion adaptacion;
    private EstadoGeneral estadoGeneral;
}

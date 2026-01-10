package com.ucacue.udipsai.modules.psicologiaeducativa;

import com.ucacue.udipsai.modules.paciente.dto.PacienteDTO;
import com.ucacue.udipsai.modules.psicologiaeducativa.components.*;
import lombok.Data;

@Data
public class PsicologiaEducativaDTO {
    private Integer id;
    private PacienteDTO paciente;
    private Boolean activo;
    
    private HistoriaEscolar historiaEscolar;
    private Desarrollo desarrollo;
    private Adaptacion adaptacion;
    private EstadoGeneral estadoGeneral;
}

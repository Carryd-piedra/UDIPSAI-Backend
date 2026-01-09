package com.ucacue.udipsai.modules.historiaclinica;

import com.ucacue.udipsai.modules.paciente.PacienteDTO;
import com.ucacue.udipsai.modules.historiaclinica.components.*;
import lombok.Data;

@Data
public class HistoriaClinicaDTO {
    private Integer id;
    private PacienteDTO paciente;
    private Boolean activo;
    private String genogramaUrl;
    
    private DatosFamiliares datosFamiliares;
    private HistoriaPrenatal historiaPrenatal;
    private HistoriaNatal historiaNatal;
    private HistoriaPostnatal historiaPostnatal;
    private DesarrolloMotor desarrolloMotor;
    private Alimentacion alimentacion;
    private AntecedentesMedicos antecedentesMedicos;
}

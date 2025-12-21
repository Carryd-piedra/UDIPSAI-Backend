package com.ucacue.udipsai.modules.fichamedica;

import com.ucacue.udipsai.modules.fichamedica.components.*;
import lombok.Data;

@Data
public class FichaMedicaRequest {
    private Integer pacienteId;
    private Boolean activo;
    private DatosFamiliares datosFamiliares;
    private HistoriaPrenatal historiaPrenatal;
    private HistoriaNatal historiaNatal;
    private HistoriaPostnatal historiaPostnatal;
    private DesarrolloMotor desarrolloMotor;
    private Alimentacion alimentacion;
    private AntecedentesMedicos antecedentesMedicos;
    // genograma file handled via MultipartFile
}

package com.ucacue.udipsai.modules.asignacion;

import lombok.Data;

@Data
public class AsignacionRequest {
    private Integer pacienteId;
    private Integer pasanteId;
    private Boolean activo;
}

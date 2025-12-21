package com.ucacue.udipsai.modules.evaluacion;

import lombok.Data;
import java.time.LocalDate;

@Data
public class EvaluacionRequest {
    private Integer pacienteId;
    private Integer especialistaId;
    private String nombreArchivo;
    private LocalDate fecha;
    private Boolean activo;
    // File will be handled via MultipartFile in controller, but this DTO holds metadata
}

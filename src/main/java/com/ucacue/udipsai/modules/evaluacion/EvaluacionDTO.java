package com.ucacue.udipsai.modules.evaluacion;

import com.ucacue.udipsai.modules.especialistas.EspecialistaDTO;
import com.ucacue.udipsai.modules.paciente.PacienteDTO;
import com.ucacue.udipsai.modules.documentos.DocumentoDTO;
import lombok.Data;
import java.time.LocalDate;

@Data
public class EvaluacionDTO {
    private Long id;
    private PacienteDTO paciente;
    private EspecialistaDTO especialista;
    private String nombreArchivo;
    private LocalDate fecha;
    private Boolean activo;
    private DocumentoDTO documento;
    // URL for direct download if needed
    private String fileUrl;
}

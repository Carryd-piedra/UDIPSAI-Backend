package com.ucacue.udipsai.modules.seguimiento;

import com.ucacue.udipsai.modules.documentos.DocumentoIdDTO;
import com.ucacue.udipsai.modules.especialistas.EspecialistaDTO;
import com.ucacue.udipsai.modules.paciente.PacienteDTO;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class SeguimientoDTO {
    private Integer id;
    private EspecialistaDTO especialista;
    private PacienteDTO paciente;
    private LocalDate fecha;
    private String observacion;
    private Boolean activo;
    private DocumentoIdDTO documento;

    public void setDocumentoId(Long id) {
        this.documento = new DocumentoIdDTO(id);
    }
}

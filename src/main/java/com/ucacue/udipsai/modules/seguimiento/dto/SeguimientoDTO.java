package com.ucacue.udipsai.modules.seguimiento;

import com.ucacue.udipsai.modules.documentos.DocumentoDTO;
import com.ucacue.udipsai.modules.especialistas.EspecialistaDTO;
import com.ucacue.udipsai.modules.paciente.dto.PacienteDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SeguimientoDTO {
    private Integer id;
    private EspecialistaDTO especialista;
    private PacienteDTO paciente;
    private LocalDate fecha;
    private String observacion;
    private Boolean activo;
    private DocumentoDTO documento;
}

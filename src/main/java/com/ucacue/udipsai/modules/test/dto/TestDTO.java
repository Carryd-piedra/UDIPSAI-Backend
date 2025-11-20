package com.ucacue.udipsai.modules.test.dto;

import com.ucacue.udipsai.modules.especialistas.dto.EspecialistasDTO;
import com.ucacue.udipsai.modules.paciente.dto.PacienteDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class TestDTO {

    private Long id;
    private PacienteDTO paciente;
    private EspecialistasDTO especialista;
    private String nombreArchivo;
    private Date fecha;
    private Integer activo;
    private Long documentoId;
    private byte[] contenido;
}

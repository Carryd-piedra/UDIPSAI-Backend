package com.ucacue.udipsai.modules.citas.dto;

import com.ucacue.udipsai.modules.citas.domain.Cita;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class CitaCriteriaDTO {
    private String search;
    private Integer id;
    private Integer idPaciente;
    private Integer idProfesional;
    private Integer idEspecialidad;
    private List<Integer> especialidades;
    private LocalDate fecha;
    private Cita.Estado estado;
}

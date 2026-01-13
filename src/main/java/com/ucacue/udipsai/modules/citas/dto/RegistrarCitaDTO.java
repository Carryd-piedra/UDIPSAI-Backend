package com.ucacue.udipsai.modules.citas.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegistrarCitaDTO {
    private Integer idPaciente;
    private Integer idProfesional;
    private Integer idEspecialidad;
    private LocalDate fecha;
    private LocalTime hora;
}

package com.ucacue.udipsai.modules.citas.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

import com.ucacue.udipsai.modules.especialidad.domain.Especialidad;
import com.fasterxml.jackson.annotation.JsonFormat;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReporteCitaDTO {
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate fecha;
    @JsonFormat(pattern = "HH:mm")
    private LocalTime hora;
    private String profesional;
    private Especialidad especialidad;
}

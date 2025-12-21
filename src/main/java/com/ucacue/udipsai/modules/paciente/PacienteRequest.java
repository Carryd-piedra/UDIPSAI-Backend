package com.ucacue.udipsai.modules.paciente;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ucacue.udipsai.modules.paciente.Paciente.JornadaEnum;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PacienteRequest {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate fechaNacimiento;
    
    private Boolean activo; 

    @NotNull
    @Size(min = 1, max = 255)
    private String nombresApellidos;

    @Size(max = 100)
    private String ciudad;

    @NotNull
    @Size(max = 15)
    private String cedula;

    private String domicilio;

    private String numeroTelefono; 
    private String numeroCelular; 

    private Integer institucionEducativaId; 
    private Integer sedeId; 

    private JornadaEnum jornada;

    private String proyecto;
    private String nivelEducativo;
    private String anioEducacion;
    private String anioUniversitario;
    private String ciclo;
    private String carrera;

    private Boolean perteneceInclusion;
    private Boolean tieneDiscapacidad;
    private Boolean portadorCarnet;
    private Boolean perteneceAProyecto;

    private String diagnostico;
    private String motivoConsulta;
    private String observaciones;
    private String tipoDiscapacidad;
    private String detalleDiscapacidad;
    private Integer porcentajeDiscapacidad;

    private Long fichaCompromisoId;
}

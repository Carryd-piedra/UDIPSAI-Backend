package com.ucacue.udipsai.modules.paciente.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ucacue.udipsai.modules.documentos.domain.model.DocumentoAdjunto;
import com.ucacue.udipsai.modules.instituciones.domain.model.InstitucionEducativa;
import com.ucacue.udipsai.modules.instituciones.domain.model.Jornada;
import com.ucacue.udipsai.modules.sedes.domain.model.Sede;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class PacienteSinImagenDTO {
    private Integer id;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date fechaApertura;

    private Integer pacienteEstado;
    private String nombresApellidos;
    private String ciudad;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date fechaNacimiento;

    private String edad;
    private String cedula;
    private String domicilio;
    private String telefono;
    private String celular;
    private InstitucionEducativa institucionEducativa;
    private String proyecto;
    private Jornada jornada;
    private String nivelEducativo;
    private String anioEducacion;
    private String anioUniversitario;
    private String ciclo;
    private String carrera;
    private String perteneceInclusion;
    private String tieneDiscapacidad;
    private boolean portadorCarnet;
    private String diagnostico;
    private String motivoConsulta;
    private String observaciones;
    private String tipoDiscapacidad;
    private String detalleDiscapacidad;
    private Integer porcentajeDiscapacidad;
    private Boolean perteneceAProyecto;
    private Sede sede;
    private List<DocumentoAdjunto> documentos_paciente;

}

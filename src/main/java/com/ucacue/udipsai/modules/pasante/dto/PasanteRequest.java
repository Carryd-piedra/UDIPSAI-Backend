package com.ucacue.udipsai.modules.pasante.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class PasanteRequest {
    private String cedula;
    private Boolean activo;
    private String nombresApellidos;
    private String email;
    private String ciudad;
    private LocalDate fechaNacimiento;
    private LocalDate inicioPasantia;
    private LocalDate finPasantia;
    private String domicilio;
    private String numeroTelefono;
    private String numeroCelular;
    private Integer sedeId;
    private Integer especialidadId;
    private Integer especialistaId;
    private String contrasenia;
}

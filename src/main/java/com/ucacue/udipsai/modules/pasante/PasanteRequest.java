package com.ucacue.udipsai.modules.pasante;

import lombok.Data;
import java.time.LocalDate;

@Data
public class PasanteRequest {
    private String cedula;
    private String nombresApellidos;
    private String contrasenia;
    private LocalDate inicioPasantia;
    private LocalDate finPasantia;
    private Integer tutorId;
    private Boolean activo;
}

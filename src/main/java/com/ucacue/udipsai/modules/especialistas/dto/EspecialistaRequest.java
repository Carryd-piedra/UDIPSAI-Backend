package com.ucacue.udipsai.modules.especialistas;

import lombok.Data;

@Data
public class EspecialistaRequest {
    private String cedula;
    private String nombresApellidos;
    private String contrasenia;
    private Integer especialidadId;
    private Integer sedeId;
    private Boolean activo;
}

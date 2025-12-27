package com.ucacue.udipsai.modules.especialistas;

import com.ucacue.udipsai.modules.sedes.SedeDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EspecialistaDTO {
    private Integer id;
    private String cedula;
    private String nombresApellidos;
    private String fotoUrl;
    private EspecialidadDTO especialidad;
    private SedeDTO sede;
    private Boolean activo;
}

package com.ucacue.udipsai.modules.especialistas;

import com.ucacue.udipsai.modules.sedes.Sede;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EspecialistaDTO {
    private Integer id; // Changed from Cedula being ID implicitly in some DTOs, to ID field? Entity has ID.
    private String cedula;
    private String nombresApellidos;
    private String fotoUrl;
    private EspecialidadDTO especialidad;
    private Sede sede;
    private Boolean activo;
}

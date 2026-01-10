package com.ucacue.udipsai.modules.especialistas.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EspecialidadDTO {
    private Integer id;
    private String area;
    private PermisosDTO permisos;
}

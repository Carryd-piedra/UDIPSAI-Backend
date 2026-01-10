package com.ucacue.udipsai.modules.pasante;

import com.ucacue.udipsai.modules.especialistas.EspecialistaDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PasanteDTO {
    private Integer id;
    private String cedula;
    private String nombresApellidos;
    private String email;
    private String ciudad;
    private LocalDate fechaNacimiento;
    private String domicilio;
    private String numeroTelefono;
    private String numeroCelular;
    private String fotoUrl;
    private LocalDate inicioPasantia;
    private LocalDate finPasantia;
    private EspecialistaDTO especialista;
    private Boolean activo;
}

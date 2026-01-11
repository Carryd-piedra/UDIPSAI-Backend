package com.ucacue.udipsai.modules.especialistas.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "permisos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Permisos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "pacientes")
    private Boolean pacientes = false;

    @Column(name = "pasantes")
    private Boolean pasantes = false;
    
    @Column(name = "sedes")
    private Boolean sedes = false;

    @Column(name = "especialistas")
    private Boolean especialistas = false;

    @Column(name = "especialidades")
    private Boolean especialidades = false;

    @Column(name = "asignaciones")
    private Boolean asignaciones = false;
    
    @Column(name = "recursos")
    private Boolean recursos = false;
    
    @Column(name = "instituciones_educativas")
    private Boolean institucionesEducativas = false;
    
    @Column(name = "historia_clinica")
    private Boolean historiaClinica = false;
    
    @Column(name = "fonoaudiologia")
    private Boolean fonoAudiologia = false;
    
    @Column(name = "psicologia_clinica")
    private Boolean psicologiaClinica = false;
    
    @Column(name = "psicologia_educativa")
    private Boolean psicologiaEducativa = false;
}

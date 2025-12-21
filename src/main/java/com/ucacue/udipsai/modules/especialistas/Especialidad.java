package com.ucacue.udipsai.modules.especialistas;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "especialidades")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Especialidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "area", nullable = false)
    private String area;

    @ManyToOne
    @JoinColumn(name = "permiso_id")
    private Permisos permisos;
    
    @Column(name = "activo")
    private Boolean activo = true;
}

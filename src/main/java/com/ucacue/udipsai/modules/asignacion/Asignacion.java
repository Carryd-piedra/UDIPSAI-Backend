package com.ucacue.udipsai.modules.asignacion;

import com.ucacue.udipsai.modules.paciente.Paciente;
import com.ucacue.udipsai.modules.pasante.Pasante;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "asignaciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Asignacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id")
    private Paciente paciente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pasante_id")
    private Pasante pasante;
    
    @Column(name = "activo", nullable = false)
    private Boolean activo = true;
}

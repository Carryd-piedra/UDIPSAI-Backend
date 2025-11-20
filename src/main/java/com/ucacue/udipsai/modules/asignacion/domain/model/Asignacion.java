package com.ucacue.udipsai.modules.asignacion.domain.model;

import com.ucacue.udipsai.modules.especialistas.domain.model.Especialistas;
import com.ucacue.udipsai.modules.paciente.domain.model.Paciente;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "asignacion")
@Getter
@Setter
public class Asignacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "paciente_id", referencedColumnName = "id")
    private Paciente paciente;

    @ManyToOne
    @JoinColumn(name = "pasante_id", referencedColumnName = "cedula")
    private Especialistas pasante;
}

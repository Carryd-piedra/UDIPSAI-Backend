package com.ucacue.udipsai.modules.documentos.domain.model;

import com.ucacue.udipsai.modules.paciente.domain.model.Paciente;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "documento")
@Getter
@Setter
public class Documento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "url", nullable = false)
    private String url;

    @ManyToOne
    @JoinColumn(name = "paciente_id")
    private Paciente paciente;
}

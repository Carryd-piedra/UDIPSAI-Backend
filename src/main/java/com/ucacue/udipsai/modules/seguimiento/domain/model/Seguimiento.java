package com.ucacue.udipsai.modules.seguimiento.domain.model;

import com.ucacue.udipsai.modules.documentos.domain.model.Documento;
import com.ucacue.udipsai.modules.especialistas.domain.model.Especialistas;
import com.ucacue.udipsai.modules.paciente.domain.model.Paciente;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.Date;

@Entity
@Table(name = "seguimiento")
@Getter
@Setter
public class Seguimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "cedulaEspecialista")
    private Especialistas especialista;

    @ManyToOne
    @JoinColumn(name = "idPaciente")
    private Paciente paciente;

    @Column(name = "fecha")
    @Temporal(TemporalType.DATE)
    private Date fecha;

    @Column(name = "observacion")
    private String observacion;

    @Column(name = "estado")
    private Integer estado = 1;

    @ManyToOne
    @JoinColumn(name = "documento_id", referencedColumnName = "id")
    private Documento documento;
}

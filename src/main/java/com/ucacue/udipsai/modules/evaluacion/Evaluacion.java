package com.ucacue.udipsai.modules.evaluacion;

import com.ucacue.udipsai.modules.especialistas.Especialista;
import com.ucacue.udipsai.modules.paciente.Paciente;
import com.ucacue.udipsai.modules.documentos.Documento;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "evaluaciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Evaluacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id")
    private Paciente paciente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "especialista_id")
    private Especialista especialista;

    @Column(name = "nombre_archivo")
    private String nombreArchivo;

    @Column(name = "fecha")
    private LocalDate fecha;

    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "documento_id", referencedColumnName = "id")
    private Documento documento;
}

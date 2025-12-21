package com.ucacue.udipsai.modules.pasante;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

import com.ucacue.udipsai.modules.especialistas.Especialista;

@Entity
@Table(name = "pasantes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pasante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "cedula", unique = true, nullable = false, length = 15)
    private String cedula;

    @Column(name = "nombres_apellidos", nullable = false)
    private String nombresApellidos;
    
    @Column(name = "contrasenia")
    private String contrasenia;

    @Column(name = "inicio_pasantia")
    private LocalDate inicioPasantia;

    @Column(name = "fin_pasantia")
    private LocalDate finPasantia;

    @Column(name = "foto_url")
    private String fotoUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tutor_id")
    private Especialista tutor;

    @Column(name = "activo", nullable = false)
    private Boolean activo = true;
}

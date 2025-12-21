package com.ucacue.udipsai.modules.fichamedica;

import com.ucacue.udipsai.modules.paciente.Paciente;
import com.ucacue.udipsai.modules.fichamedica.components.*;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "fichas_medicas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FichaMedica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id", referencedColumnName = "id")
    private Paciente paciente;

    @Column(name = "activo", nullable = false)
    private Boolean activo = true;
    
    @Column(name = "genograma_url")
    private String genogramaUrl;

    // --- Componentes @Embeddable ---

    @Embedded
    private DatosFamiliares datosFamiliares;

    @Embedded
    private HistoriaPrenatal historiaPrenatal;

    @Embedded
    private HistoriaNatal historiaNatal;

    @Embedded
    private HistoriaPostnatal historiaPostnatal;

    @Embedded
    private DesarrolloMotor desarrolloMotor;

    @Embedded
    private Alimentacion alimentacion;

    @Embedded
    private AntecedentesMedicos antecedentesMedicos;

    // --- Enums ---

    public enum Parto {
        NORMAL, CESÁREA
    }

    public enum LlantoAlNacer {
        INMEDIATO, AL_ESTÍMULO, DEMORADO
    }

    public enum CordonOmbilical {
        CUELLO, CUERPO, OTRO
    }

    public enum PresenciaIctericia {
        SI, NO
    }

    public enum TransfucionSangre {
        SI, NO
    }
}

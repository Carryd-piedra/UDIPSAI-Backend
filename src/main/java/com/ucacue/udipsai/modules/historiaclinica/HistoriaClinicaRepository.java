package com.ucacue.udipsai.modules.historiaclinica;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HistoriaClinicaRepository extends JpaRepository<HistoriaClinica, Integer> {
    HistoriaClinica findByPacienteIdAndActivo(Integer idPaciente, boolean activo);
}

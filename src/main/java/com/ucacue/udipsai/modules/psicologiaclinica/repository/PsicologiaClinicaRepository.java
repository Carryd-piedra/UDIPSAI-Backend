package com.ucacue.udipsai.modules.psicologiaclinica;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PsicologiaClinicaRepository extends JpaRepository<PsicologiaClinica, Integer> {
    PsicologiaClinica findByPacienteIdAndActivo(Integer idPaciente, boolean activo);
    List<PsicologiaClinica> findByActivo(boolean activo);
}

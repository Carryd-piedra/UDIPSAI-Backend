package com.ucacue.udipsai.modules.psicologiaclinica.domain.repository;

import com.ucacue.udipsai.modules.psicologiaclinica.domain.model.PsicologiaClinica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PsicologiaClinicaRepository extends JpaRepository<PsicologiaClinica, Integer> {
    List<PsicologiaClinica> findByEstado(int estado);
    List<PsicologiaClinica> findByPacienteId(int idPaciente);
}

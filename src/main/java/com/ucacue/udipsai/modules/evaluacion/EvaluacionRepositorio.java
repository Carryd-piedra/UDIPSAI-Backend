package com.ucacue.udipsai.modules.evaluacion;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EvaluacionRepositorio extends JpaRepository<Evaluacion, Long>, JpaSpecificationExecutor<Evaluacion> {
    List<Evaluacion> findByActivoTrue();
    List<Evaluacion> findByPacienteIdAndActivoTrue(Integer pacienteId);
    List<Evaluacion> findByPacienteIdAndEspecialistaCedulaAndActivoTrue(Integer pacienteId, String cedulaEspecialista);
}

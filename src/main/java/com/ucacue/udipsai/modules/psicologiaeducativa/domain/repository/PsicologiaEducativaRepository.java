package com.ucacue.udipsai.modules.psicologiaeducativa.domain.repository;

import com.ucacue.udipsai.modules.psicologiaeducativa.domain.model.PsicologiaEducativa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PsicologiaEducativaRepository extends JpaRepository<PsicologiaEducativa, Integer> {
    List<PsicologiaEducativa> findByPacienteIdAndEstado(int idPaciente, int estado);
    List<PsicologiaEducativa> findByEstado(int estado);

}

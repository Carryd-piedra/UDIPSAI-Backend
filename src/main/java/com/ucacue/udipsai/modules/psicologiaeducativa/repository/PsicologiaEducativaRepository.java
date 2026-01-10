package com.ucacue.udipsai.modules.psicologiaeducativa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PsicologiaEducativaRepository extends JpaRepository<PsicologiaEducativa, Integer> {
    PsicologiaEducativa findByPacienteIdAndActivo(Integer idPaciente, boolean activo);
    List<PsicologiaEducativa> findByActivo(boolean activo);
}

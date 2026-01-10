package com.ucacue.udipsai.modules.fonoaudiologia;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FonoaudiologiaRepository extends JpaRepository<Fonoaudiologia, Integer> {
    Fonoaudiologia findByPacienteIdAndActivo(Integer idPaciente, boolean activo);
    List<Fonoaudiologia> findByActivo(boolean activo);
}

package com.ucacue.udipsai.modules.fichamedica;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FichaMedicaRepository extends JpaRepository<FichaMedica, Integer> {
    FichaMedica findByPacienteIdAndActivo(Integer idPaciente, boolean activo);
    List<FichaMedica> findByActivo(boolean activo);
}

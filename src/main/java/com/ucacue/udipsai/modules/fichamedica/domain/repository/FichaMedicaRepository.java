package com.ucacue.udipsai.modules.fichamedica.domain.repository;

import com.ucacue.udipsai.modules.fichamedica.domain.model.FichaMedica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FichaMedicaRepository extends JpaRepository<FichaMedica, Integer> {
    List<FichaMedica> findByEstado(int estado);
    List<FichaMedica> findByPacienteId(int idPaciente);
}

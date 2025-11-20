package com.ucacue.udipsai.modules.especialistas.domain.repository;

import com.ucacue.udipsai.modules.especialistas.domain.model.Especialidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EspecialidadRepositorio extends JpaRepository<Especialidad, Integer> {
}

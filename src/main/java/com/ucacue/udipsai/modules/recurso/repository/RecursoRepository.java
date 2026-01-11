package com.ucacue.udipsai.modules.recurso.repository;
import com.ucacue.udipsai.modules.recurso.domain.Recurso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecursoRepositorio extends JpaRepository<Recurso, Long>{
}

package com.ucacue.udipsai.modules.documentos.domain.repository;

import com.ucacue.udipsai.modules.documentos.domain.model.Documento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentoRepositorio extends JpaRepository<Documento, Long> {
}

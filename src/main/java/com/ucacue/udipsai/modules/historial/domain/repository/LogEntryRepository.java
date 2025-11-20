package com.ucacue.udipsai.modules.historial.domain.repository;

import com.ucacue.udipsai.modules.historial.domain.model.LogEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogEntryRepository extends JpaRepository<LogEntry, Long> {
}

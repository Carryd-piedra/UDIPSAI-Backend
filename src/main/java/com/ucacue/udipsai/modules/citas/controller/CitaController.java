package com.ucacue.udipsai.modules.citas.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ucacue.udipsai.modules.citas.dto.CitaCriteriaDTO;
import com.ucacue.udipsai.modules.citas.dto.CitaDTO;
import com.ucacue.udipsai.modules.citas.dto.RegistrarCitaDTO;
import com.ucacue.udipsai.modules.citas.dto.ReporteCitaRespuestaDTO;
import com.ucacue.udipsai.modules.citas.service.CitaService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/citas")
@Slf4j
public class CitaController {

    @Autowired
    private CitaService citaServ;

    @GetMapping("/filter")
    @PreAuthorize("hasAuthority('PERM_CITAS')")
    public ResponseEntity<Page<CitaDTO>> filtrarCitas(
            CitaCriteriaDTO criteria,
            @PageableDefault(page = 0, size = 5) Pageable pageable) {
        log.info("GET /api/citas/filter with criteria: {}", criteria);
        return ResponseEntity.ok(citaServ.filtrarCitas(criteria, pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PERM_CITAS')")
    public ResponseEntity<CitaDTO> obtenerCita(@PathVariable Integer id) {
        log.info("GET /api/citas/{}", id);
        return ResponseEntity.ok(citaServ.obtenerCitaPorId(id));
    }

    @GetMapping("/horas-libres/{profesionalId}/")
    @PreAuthorize("hasAuthority('PERM_CITAS')")
    public ResponseEntity<List<String>> encontrarHorasLibresProfesional(@PathVariable Integer profesionalId,
            @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate fecha) {
        return ResponseEntity.ok(citaServ.encontrarHorasLibresProfesional(profesionalId, fecha));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('PERM_CITAS_CREAR')")
    public ResponseEntity<CitaDTO> registrarCita(@RequestBody RegistrarCitaDTO cita) {
        log.info("POST /api/citas");
        return ResponseEntity.ok(citaServ.registrarCita(cita));
    }

    @PutMapping("/reagendar/{id}")
    @PreAuthorize("hasAuthority('PERM_CITAS_EDITAR')")
    public ResponseEntity<CitaDTO> reagendarCita(@PathVariable Integer id, @RequestBody RegistrarCitaDTO cita) {
        log.info("PUT /api/citas/reagendar/{}", id);
        return ResponseEntity.ok(citaServ.reagendarCita(id, cita));
    }

    @PatchMapping("/falta-justificada/{id}")
    @PreAuthorize("hasAuthority('PERM_CITAS_EDITAR')")
    public ResponseEntity<?> faltaJustificada(@PathVariable Integer id) {
        citaServ.faltaJustificada(id);
        return ResponseEntity.ok().body("Cita asignada como FALTA JUSTIFICADA correctamente");
    }

    @PatchMapping("/falta-injustificada/{id}")
    @PreAuthorize("hasAuthority('PERM_CITAS_EDITAR')")
    public ResponseEntity<?> faltaInjustificada(@PathVariable Integer id) {
        citaServ.faltaInjustificada(id);
        return ResponseEntity.ok().body("Cita asignada como FALTA INJUSTIFICADA correctamente");
    }

    @PatchMapping("/finalizar/{id}")
    @PreAuthorize("hasAuthority('PERM_CITAS_EDITAR')")
    public ResponseEntity<?> finalizarCita(@PathVariable Integer id) {
        citaServ.finalizarCita(id);
        return ResponseEntity.ok().body("Cita finalizada correctamente");
    }

    @PatchMapping("/cancelar/{id}")
    @PreAuthorize("hasAuthority('PERM_CITAS_ELIMINAR')")
    public ResponseEntity<?> cancelarCita(@PathVariable Integer id) {
        citaServ.cancelarCita(id);
        return ResponseEntity.ok().body("Cita cancelada correctamente");
    }

    @GetMapping("/reporte/paciente/{id}")
    @PreAuthorize("hasAuthority('PERM_CITAS')")
    public ResponseEntity<ReporteCitaRespuestaDTO> obtenerReportePorPaciente(@PathVariable Integer id) {
        return ResponseEntity.ok(citaServ.generarReportePorPaciente(id));
    }
}

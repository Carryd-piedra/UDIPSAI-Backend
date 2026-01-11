package com.ucacue.udipsai.modules.psicologiaclinica.controller;

import com.ucacue.udipsai.modules.psicologiaclinica.dto.PsicologiaClinicaDTO;
import com.ucacue.udipsai.modules.psicologiaclinica.dto.PsicologiaClinicaRequest;
import com.ucacue.udipsai.modules.psicologiaclinica.service.PsicologiaClinicaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@RestController
@RequestMapping("/api/psicologia-clinica")
@Slf4j
public class PsicologiaClinicaController {

    @Autowired
    private PsicologiaClinicaService service;

    @GetMapping
    @PreAuthorize("hasAuthority('PERM_PSICOLOGIA_CLINICA')")
    public ResponseEntity<List<PsicologiaClinicaDTO>> listarFichasPsicologiaClinica() {
        log.info("Petición GET para listar todas las fichas de psicología clínica activas");
        return ResponseEntity.ok(service.listarFichasPsicologiaClinica());
    }

    @GetMapping("/paciente/{pacienteId}")
    @PreAuthorize("hasAuthority('PERM_PSICOLOGIA_CLINICA') and @asignacionSecurity.checkPasanteAcceso(#pacienteId)")
    public ResponseEntity<PsicologiaClinicaDTO> obtenerFichaPsicologiaClinicaPorPacienteId(@PathVariable Integer pacienteId) {
        log.info("Petición GET para obtener ficha de psicología clínica por paciente ID: {}", pacienteId);
        PsicologiaClinicaDTO ficha = service.obtenerFichaPsicologiaClinicaPorPacienteId(pacienteId);
        if (ficha != null) {
            return ResponseEntity.ok(ficha);
        }
        log.warn("Ficha de psicología clínica no encontrada para paciente ID: {}", pacienteId);
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    @PreAuthorize("hasAuthority('PERM_PSICOLOGIA_CLINICA_CREAR') and @asignacionSecurity.checkPasanteAcceso(#request.pacienteId)")
    public ResponseEntity<PsicologiaClinicaDTO> crearFichaPsicologiaClinica(@RequestBody PsicologiaClinicaRequest request) {
        log.info("Petición POST para crear ficha de psicología clínica para paciente ID: {}", request.getPacienteId());
        try {
            return ResponseEntity.ok(service.crearFichaPsicologiaClinica(request));
        } catch (IllegalStateException e) {
             log.warn("Intento de crear ficha duplicada: {}", e.getMessage());
             return ResponseEntity.status(org.springframework.http.HttpStatus.CONFLICT).build();
        } catch (Exception e) {
            log.error("Error al crear ficha de psicología clínica: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('PERM_PSICOLOGIA_CLINICA_EDITAR')")
    public ResponseEntity<PsicologiaClinicaDTO> actualizarFichaPsicologiaClinica(@PathVariable Integer id, @RequestBody PsicologiaClinicaRequest request) {
        log.info("Petición PUT para actualizar ficha de psicología clínica ID: {}", id);
        try {
             return ResponseEntity.ok(service.actualizarFichaPsicologiaClinica(id, request));
        } catch (Exception e) {
            log.error("Error al actualizar ficha de psicología clínica: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('PERM_PSICOLOGIA_CLINICA_ELIMINAR')")
    public ResponseEntity<Void> eliminarFichaPsicologiaClinica(@PathVariable Integer id) {
        log.info("Petición DELETE para eliminar ficha de psicología clínica ID: {}", id);
        service.eliminarFichaPsicologiaClinica(id);
        return ResponseEntity.noContent().build();
    }
}

package com.ucacue.udipsai.modules.psicologiaclinica;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@RestController
@RequestMapping("/api/psicologia-clinica")
@CrossOrigin(origins = "*")
@Slf4j
public class PsicologiaClinicaController {

    @Autowired
    private PsicologiaClinicaService service;

    @GetMapping
    public ResponseEntity<List<PsicologiaClinicaDTO>> listarFichasPsicologiaClinica() {
        log.info("Petición GET para listar todas las fichas de psicología clínica activas");
        return ResponseEntity.ok(service.listarFichasPsicologiaClinica());
    }

    @GetMapping("/paciente/{pacienteId}")
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
    public ResponseEntity<PsicologiaClinicaDTO> guardarFichaPsicologiaClinica(@RequestBody PsicologiaClinicaRequest request) {
        log.info("Petición POST para guardar/actualizar ficha de psicología clínica para paciente ID: {}", request.getPacienteId());
        try {
            return ResponseEntity.ok(service.guardarFichaPsicologiaClinica(request));
        } catch (Exception e) {
            log.error("Error al guardar ficha de psicología clínica: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarFichaPsicologiaClinica(@PathVariable Integer id) {
        log.info("Petición DELETE para eliminar ficha de psicología clínica ID: {}", id);
        service.eliminarFichaPsicologiaClinica(id);
        return ResponseEntity.noContent().build();
    }
}

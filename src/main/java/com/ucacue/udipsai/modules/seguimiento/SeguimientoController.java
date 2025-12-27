package com.ucacue.udipsai.modules.seguimiento;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@RestController
@RequestMapping("/api/seguimientos")
@CrossOrigin(origins = "*")
@Slf4j
public class SeguimientoController {

    @Autowired
    private SeguimientoService seguimientoService;

    @GetMapping
    public ResponseEntity<List<SeguimientoDTO>> listarSeguimientosActivos() {
        log.info("Petición GET para listar todos los seguimientos activos");
        return ResponseEntity.ok(seguimientoService.listarSeguimientosActivos());
    }

    @GetMapping("/paciente/{pacienteId}")
    public ResponseEntity<List<SeguimientoDTO>> listarSeguimientosPorPacienteId(@PathVariable Integer pacienteId) {
        log.info("Petición GET para listar seguimientos por paciente ID: {}", pacienteId);
        return ResponseEntity.ok(seguimientoService.listarSeguimientosPorPacienteId(pacienteId));
    }

    @PostMapping
    public ResponseEntity<SeguimientoDTO> crearSeguimiento(@RequestBody SeguimientoRequest request) {
        log.info("Petición POST para crear seguimiento");
        try {
            return ResponseEntity.ok(seguimientoService.crearSeguimiento(request));
        } catch (Exception e) {
            log.error("Error al crear seguimiento: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<SeguimientoDTO> actualizarSeguimiento(@PathVariable Integer id, @RequestBody SeguimientoRequest request) {
        log.info("Petición PUT para actualizar seguimiento ID: {}", id);
        try {
            return ResponseEntity.ok(seguimientoService.actualizarSeguimiento(id, request));
        } catch (Exception e) {
            log.error("Error al actualizar seguimiento ID {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarSeguimiento(@PathVariable Integer id) {
        log.info("Petición DELETE para eliminar seguimiento ID: {}", id);
        seguimientoService.eliminarSeguimiento(id);
        return ResponseEntity.noContent().build();
    }
}

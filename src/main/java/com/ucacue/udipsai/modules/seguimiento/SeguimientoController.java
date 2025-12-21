package com.ucacue.udipsai.modules.seguimiento;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/seguimientos")
@CrossOrigin(origins = "*")
public class SeguimientoController {

    @Autowired
    private SeguimientoService seguimientoService;

    @GetMapping
    public List<SeguimientoDTO> getAllSeguimientos() {
        return seguimientoService.getAllSeguimientos();
    }

    @GetMapping("/paciente/{pacienteId}")
    public List<SeguimientoDTO> getSeguimientosByPacienteId(@PathVariable Integer pacienteId) {
        return seguimientoService.getSeguimientosByPacienteId(pacienteId);
    }

    @PostMapping
    public ResponseEntity<SeguimientoDTO> createSeguimiento(@RequestBody SeguimientoRequest request) {
        return ResponseEntity.ok(seguimientoService.createSeguimiento(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SeguimientoDTO> updateSeguimiento(@PathVariable Integer id, @RequestBody SeguimientoRequest request) {
        return ResponseEntity.ok(seguimientoService.updateSeguimiento(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSeguimiento(@PathVariable Integer id) {
        seguimientoService.deleteSeguimiento(id);
        return ResponseEntity.noContent().build();
    }
}

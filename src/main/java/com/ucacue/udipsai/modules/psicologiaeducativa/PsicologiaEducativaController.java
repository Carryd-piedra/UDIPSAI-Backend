package com.ucacue.udipsai.modules.psicologiaeducativa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/psicologia-educativa")
@CrossOrigin(origins = "*")
public class PsicologiaEducativaController {

    @Autowired
    private PsicologiaEducativaService service;

    @GetMapping
    public List<PsicologiaEducativaDTO> getAll() {
        return service.getAll();
    }

    @GetMapping("/paciente/{pacienteId}")
    public ResponseEntity<PsicologiaEducativaDTO> getByPaciente(@PathVariable Integer pacienteId) {
        PsicologiaEducativaDTO ficha = service.getByPacienteId(pacienteId);
        if (ficha != null) {
            return ResponseEntity.ok(ficha);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<PsicologiaEducativaDTO> createUpdate(@RequestBody PsicologiaEducativaRequest request) {
        return ResponseEntity.ok(service.createUpdate(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}

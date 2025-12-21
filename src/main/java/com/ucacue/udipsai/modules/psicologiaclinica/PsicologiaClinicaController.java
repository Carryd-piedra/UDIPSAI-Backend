package com.ucacue.udipsai.modules.psicologiaclinica;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/psicologia-clinica")
@CrossOrigin(origins = "*")
public class PsicologiaClinicaController {

    @Autowired
    private PsicologiaClinicaService service;

    @GetMapping
    public List<PsicologiaClinicaDTO> getAll() {
        return service.getAll();
    }

    @GetMapping("/paciente/{pacienteId}")
    public ResponseEntity<PsicologiaClinicaDTO> getByPaciente(@PathVariable Integer pacienteId) {
        PsicologiaClinicaDTO ficha = service.getByPacienteId(pacienteId);
        if (ficha != null) {
            return ResponseEntity.ok(ficha);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<PsicologiaClinicaDTO> createUpdate(@RequestBody PsicologiaClinicaRequest request) {
        return ResponseEntity.ok(service.createUpdate(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}

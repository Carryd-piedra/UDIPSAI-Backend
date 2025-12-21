package com.ucacue.udipsai.modules.fonoaudiologia;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/fonoaudiologia")
@CrossOrigin(origins = "*")
public class FonoaudiologiaController {

    @Autowired
    private FonoaudiologiaService service;

    @GetMapping
    public List<FonoaudiologiaDTO> getAll() {
        return service.getAll();
    }

    @GetMapping("/paciente/{pacienteId}")
    public ResponseEntity<FonoaudiologiaDTO> getByPaciente(@PathVariable Integer pacienteId) {
        FonoaudiologiaDTO ficha = service.getByPacienteId(pacienteId);
        if (ficha != null) {
            return ResponseEntity.ok(ficha);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<FonoaudiologiaDTO> createUpdate(@RequestBody FonoaudiologiaRequest request) {
        return ResponseEntity.ok(service.createUpdate(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}

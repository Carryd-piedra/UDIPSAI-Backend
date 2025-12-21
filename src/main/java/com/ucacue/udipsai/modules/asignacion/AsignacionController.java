package com.ucacue.udipsai.modules.asignacion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/asignaciones")
@CrossOrigin(origins = "*")
public class AsignacionController {

    @Autowired
    private AsignacionService asignacionService;

    @GetMapping
    public List<AsignacionDTO> getAllAsignaciones() {
        return asignacionService.getAllAsignaciones();
    }

    @PostMapping
    public ResponseEntity<AsignacionDTO> createAsignacion(@RequestBody AsignacionRequest request) {
        return ResponseEntity.ok(asignacionService.createAsignacion(request));
    }
    
    @GetMapping("/pasante/{pasanteId}")
    public List<AsignacionDTO> getAsignacionesByPasante(@PathVariable Integer pasanteId) {
        return asignacionService.getAsignacionesByPasante(pasanteId);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAsignacion(@PathVariable Long id) {
        asignacionService.deleteAsignacion(id);
        return ResponseEntity.noContent().build();
    }
}

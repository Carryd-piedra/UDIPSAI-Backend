package com.ucacue.udipsai.modules.evaluacion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/evaluaciones")
@CrossOrigin(origins = "*")
public class EvaluacionController {

    @Autowired
    private EvaluacionService evaluacionService;

    @GetMapping
    public List<EvaluacionDTO> getAllEvaluaciones() {
        return evaluacionService.getAllEvaluaciones();
    }

    @GetMapping("/paciente/{pacienteId}")
    public List<EvaluacionDTO> getEvaluacionesByPaciente(@PathVariable Integer pacienteId) {
        return evaluacionService.getEvaluacionesByPaciente(pacienteId);
    }

    @PostMapping
    public ResponseEntity<EvaluacionDTO> createEvaluacion(
            @RequestParam("data") String data,
            @RequestParam(value = "file", required = false) MultipartFile file) throws IOException {
        
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules(); // Support Java 8 dates
        EvaluacionRequest request = mapper.readValue(data, EvaluacionRequest.class);
        
        return ResponseEntity.ok(evaluacionService.createEvaluacion(request, file));
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long id) {
        Resource file = evaluacionService.loadFileAsResource(id);
        if (file == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                .body(file);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvaluacion(@PathVariable Long id) {
        evaluacionService.deleteEvaluacion(id);
        return ResponseEntity.noContent().build();
    }
}

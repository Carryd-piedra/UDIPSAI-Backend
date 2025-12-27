package com.ucacue.udipsai.modules.evaluacion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/evaluaciones")
@CrossOrigin(origins = "*")
@Slf4j
public class EvaluacionController {

    @Autowired
    private EvaluacionService evaluacionService;

    @GetMapping
    public List<EvaluacionDTO> listarEvaluaciones() {
        log.info("Petición GET para listar todas las evaluaciones activas");
        return evaluacionService.listarEvaluaciones();
    }

    @GetMapping("/paciente/{pacienteId}")
    public List<EvaluacionDTO> listarEvaluacionesPorPacienteId(@PathVariable Integer pacienteId) {
        log.info("Petición GET para listar evaluaciones del paciente ID: {}", pacienteId);
        return evaluacionService.listarEvaluacionesPorPacienteId(pacienteId);
    }

    @PostMapping
    public ResponseEntity<EvaluacionDTO> crearEvaluacion(
            @RequestParam("data") String data,
            @RequestParam(value = "file", required = false) MultipartFile file) throws IOException {
        
        log.info("Petición POST para crear evaluación. Data: {}", data);
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.findAndRegisterModules(); 
            EvaluacionRequest request = mapper.readValue(data, EvaluacionRequest.class);
            EvaluacionDTO created = evaluacionService.crearEvaluacion(request, file);
            return ResponseEntity.ok(created);
        } catch (IOException e) {
            log.error("Error al procesar JSON o Archivo en creación: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error al crear evaluación: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}/descargar")
    public ResponseEntity<Resource> descargarArchivoEvaluacion(@PathVariable Long id) {
        log.info("Petición GET para descargar archivo de evaluacion ID: {}", id);
        Resource file = evaluacionService.cargarArchivoComoRecurso(id);
        if (file == null) {
            log.warn("Archivo de evaluación ID {} no no encontrado", id);
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                .body(file);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarEvaluacion(@PathVariable Long id) {
        log.info("Petición DELETE para eliminar evaluación ID: {}", id);
        evaluacionService.eliminarEvaluacion(id);
        return ResponseEntity.noContent().build();
    }
}

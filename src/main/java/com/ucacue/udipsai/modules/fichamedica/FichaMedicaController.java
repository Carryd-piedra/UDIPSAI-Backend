package com.ucacue.udipsai.modules.fichamedica;

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
@RequestMapping("/api/ficha-medica")
@CrossOrigin(origins = "*")
@Slf4j
public class FichaMedicaController {

    @Autowired
    private FichaMedicaService fichaMedicaService;

    @GetMapping
    public List<FichaMedicaDTO> listarFichasMedicas() {
        log.info("Petición GET para listar todas las fichas médicas activas");
        return fichaMedicaService.listarFichasMedicas();
    }

    @GetMapping("/paciente/{pacienteId}")
    public ResponseEntity<FichaMedicaDTO> obtenerFichaMedicaPorPacienteId(@PathVariable Integer pacienteId) {
        log.info("Petición GET para obtener ficha médica del paciente ID: {}", pacienteId);
        FichaMedicaDTO ficha = fichaMedicaService.obtenerFichaMedicaPorPacienteId(pacienteId);
        if (ficha != null) {
            return ResponseEntity.ok(ficha);
        }
        log.warn("Ficha médica no encontrada para paciente ID: {}", pacienteId);
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<FichaMedicaDTO> guardarFichaMedica(
            @RequestParam("data") String data,
            @RequestParam(value = "genograma", required = false) MultipartFile genograma) throws IOException {
        
        log.info("Petición POST para guardar ficha médica. Data: {}", data);
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.findAndRegisterModules(); 
            FichaMedicaRequest request = mapper.readValue(data, FichaMedicaRequest.class);
            FichaMedicaDTO saved = fichaMedicaService.guardarFichaMedica(request, genograma);
            return ResponseEntity.ok(saved);
        } catch (IOException e) {
            log.error("Error al procesar JSON o Archivo en guardado de ficha médica: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error al guardar ficha médica: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/paciente/{pacienteId}/genograma")
    public ResponseEntity<Resource> descargarGenograma(@PathVariable Integer pacienteId) {
        log.info("Petición GET para descargar genograma del paciente ID: {}", pacienteId);
        Resource file = fichaMedicaService.cargarGenogramaComoRecurso(pacienteId);
        if (file == null) {
            log.warn("Genograma no encontrado para paciente ID: {}", pacienteId);
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                .body(file);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarFichaMedica(@PathVariable Integer id) {
        log.info("Petición DELETE para eliminar ficha médica ID: {}", id);
        fichaMedicaService.eliminarFichaMedica(id);
        return ResponseEntity.noContent().build();
    }
}

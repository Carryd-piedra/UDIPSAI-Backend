package com.ucacue.udipsai.modules.paciente;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ucacue.udipsai.modules.reportes.ExcelService;
import com.ucacue.udipsai.modules.reportes.ReporteGeneralService;
import com.ucacue.udipsai.modules.storage.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@RestController
@RequestMapping("/api/pacientes")
@CrossOrigin(origins = "*")
public class PacienteController {

    @Autowired
    private PacienteService pacienteService;

    @Autowired
    private StorageService storageService;

    @Autowired
    private ExcelService excelService;

    @Autowired
    private ReporteGeneralService reporteGeneralService;
    
    // --- Image Serving ---
    @GetMapping("/fotos/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        Resource file = storageService.loadAsResource(filename);
        if (file == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getFilename() + "\"")
            .body(file);
    }

    @GetMapping("/listar")
    public ResponseEntity<List<PacienteDTO>> getAllPacientes() {
        return ResponseEntity.ok(pacienteService.getAllPacientes());
    }

    @GetMapping("/listar/{id}")
    public ResponseEntity<PacienteDTO> getPacienteById(@PathVariable Integer id) {
        PacienteDTO dto = pacienteService.getPacienteDTOById(id);
        if (dto != null) {
            return ResponseEntity.ok(dto);
        }
        return ResponseEntity.notFound().build();
    }

    // --- Create with Multipart (JSON + Image) ---
    @PostMapping(value = "/insertar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createPaciente(
            @RequestPart("data") String dataJson,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        try {
            // Manual mapping because RequestPart(json) + File is tricky in some Spring versions when Strict
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            PacienteRequest request = mapper.readValue(dataJson, PacienteRequest.class);
            
            PacienteDTO created = pacienteService.createPaciente(request, file);
            return new ResponseEntity<>(created, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error creating paciente: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping(value = "/actualizar/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updatePaciente(
            @PathVariable Integer id,
            @RequestPart("data") String dataJson,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            PacienteRequest request = mapper.readValue(dataJson, PacienteRequest.class);

            PacienteDTO updated = pacienteService.updatePaciente(id, request, file);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return new ResponseEntity<>("Error updating paciente: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/buscar")
    public ResponseEntity<List<PacienteDTO>> buscarPacientes(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "sedeId", required = false) Integer sedeId) {

        List<PacienteDTO> pacientes = pacienteService.searchPacientes(search, sedeId);
        return ResponseEntity.ok(pacientes);
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<?> deletePaciente(@PathVariable Integer id) {
        try {
            pacienteService.deletePaciente(id);
            return ResponseEntity.ok("Paciente eliminado (inactivado)");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    
    // --- Document Methods (Simplified) ---

    @PostMapping("/{id}/documento")
    public ResponseEntity<?> subirDocumento(@PathVariable Integer id, @RequestParam("file") MultipartFile file) {
        try {
            return ResponseEntity.ok(pacienteService.addDocumentoToPaciente(id, file));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    
    @PostMapping("/upload")
    public ResponseEntity<?> subirPacientesExcel(@RequestParam("file") MultipartFile file) {
        try {
            List<String> messages = excelService.savePatientsFromExcel(file);
            return ResponseEntity.ok(messages);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error al subir pacientes desde Excel: " + e.getMessage());
        }
    }
    
    // Keeping report endpoint
    @GetMapping("/{id}/reporte-general")
    public ResponseEntity<byte[]> obtenerReporteGeneralPorPacienteId(@PathVariable Integer id) {
        try {
            byte[] pdfContent = reporteGeneralService.generarReporteGeneralPorPacienteId(id);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=reporte_general_paciente_" + id + ".pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfContent);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(null); 
        }
    }
}


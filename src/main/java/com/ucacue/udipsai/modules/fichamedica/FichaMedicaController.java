package com.ucacue.udipsai.modules.fichamedica;

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
@RequestMapping("/ficha-medica")
@CrossOrigin(origins = "*")
public class FichaMedicaController {

    @Autowired
    private FichaMedicaService fichaMedicaService;

    @GetMapping
    public List<FichaMedicaDTO> getAllFichas() {
        return fichaMedicaService.getAllFichas();
    }

    @GetMapping("/paciente/{pacienteId}")
    public ResponseEntity<FichaMedicaDTO> getFichaByPaciente(@PathVariable Integer pacienteId) {
        FichaMedicaDTO ficha = fichaMedicaService.getFichaByPacienteId(pacienteId);
        if (ficha != null) {
            return ResponseEntity.ok(ficha);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<FichaMedicaDTO> createUpdateFicha(
            @RequestParam("data") String data,
            @RequestParam(value = "genograma", required = false) MultipartFile genograma) throws IOException {
        
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules(); 
        FichaMedicaRequest request = mapper.readValue(data, FichaMedicaRequest.class);
        
        return ResponseEntity.ok(fichaMedicaService.createUpdateFicha(request, genograma));
    }

    @GetMapping("/paciente/{pacienteId}/genograma")
    public ResponseEntity<Resource> getGenograma(@PathVariable Integer pacienteId) {
        Resource file = fichaMedicaService.loadGenogramaAsResource(pacienteId);
        if (file == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                .body(file);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFicha(@PathVariable Integer id) {
        fichaMedicaService.deleteFicha(id);
        return ResponseEntity.noContent().build();
    }
}

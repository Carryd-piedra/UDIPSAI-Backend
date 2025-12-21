package com.ucacue.udipsai.modules.pasante;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ucacue.udipsai.modules.storage.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/pasantes")
@CrossOrigin(origins = "*")
public class PasanteController {

    @Autowired
    private PasanteService pasanteService;

    @Autowired
    private StorageService storageService;

    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping
    public List<PasanteDTO> getAllPasantes() {
        return pasanteService.getAllPasantes();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PasanteDTO> getPasanteById(@PathVariable Integer id) {
        PasanteDTO pasante = pasanteService.getPasanteById(id);
        if (pasante != null) {
            return ResponseEntity.ok(pasante);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/buscar")
    public List<PasanteDTO> searchPasantes(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Integer tutorId) {
        return pasanteService.searchPasantes(search, tutorId);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createPasante(
            @RequestPart("data") String dataJson,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        try {
            PasanteRequest request = objectMapper.readValue(dataJson, PasanteRequest.class);
            PasanteDTO created = pasanteService.createPasante(request, file);
            return ResponseEntity.ok(created);
        } catch (JsonProcessingException e) {
            return ResponseEntity.badRequest().body("Error parsing JSON");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error creating pasante: " + e.getMessage());
        }
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updatePasante(
            @PathVariable Integer id,
            @RequestPart("data") String dataJson,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        try {
            PasanteRequest request = objectMapper.readValue(dataJson, PasanteRequest.class);
            PasanteDTO updated = pasanteService.updatePasante(id, request, file);
            return ResponseEntity.ok(updated);
        } catch (JsonProcessingException e) {
            return ResponseEntity.badRequest().body("Error parsing JSON");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error updating pasante: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePasante(@PathVariable Integer id) {
        pasanteService.deletePasante(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/fotos/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        Resource file = storageService.loadAsResource(filename);
        if (file == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getFilename() + "\"")
                .body(file);
    }
}

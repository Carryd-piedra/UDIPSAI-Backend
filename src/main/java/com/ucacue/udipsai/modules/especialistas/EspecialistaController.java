package com.ucacue.udipsai.modules.especialistas;

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
@RequestMapping("/especialistas")
@CrossOrigin(origins = "*")
public class EspecialistaController {

    @Autowired
    private EspecialistaService especialistaService;

    @Autowired
    private StorageService storageService;

    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping
    public List<EspecialistaDTO> getAllEspecialistas() {
        return especialistaService.getAllEspecialistas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<EspecialistaDTO> getEspecialistaById(@PathVariable Integer id) {
        EspecialistaDTO especialista = especialistaService.getEspecialistaById(id);
        if (especialista != null) {
            return ResponseEntity.ok(especialista);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/buscar")
    public List<EspecialistaDTO> searchEspecialistas(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Integer especialidadId,
            @RequestParam(required = false) Integer sedeId) {
        return especialistaService.searchEspecialistas(search, especialidadId, sedeId);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createEspecialista(
            @RequestPart("data") String dataJson,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        try {
            EspecialistaRequest request = objectMapper.readValue(dataJson, EspecialistaRequest.class);
            EspecialistaDTO created = especialistaService.createEspecialista(request, file);
            return ResponseEntity.ok(created);
        } catch (JsonProcessingException e) {
            return ResponseEntity.badRequest().body("Error parsing JSON");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error creating especialista: " + e.getMessage());
        }
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateEspecialista(
            @PathVariable Integer id,
            @RequestPart("data") String dataJson,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        try {
            EspecialistaRequest request = objectMapper.readValue(dataJson, EspecialistaRequest.class);
            EspecialistaDTO updated = especialistaService.updateEspecialista(id, request, file);
            return ResponseEntity.ok(updated);
        } catch (JsonProcessingException e) {
            return ResponseEntity.badRequest().body("Error parsing JSON");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error updating especialista: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEspecialista(@PathVariable Integer id) {
        especialistaService.deleteEspecialista(id);
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

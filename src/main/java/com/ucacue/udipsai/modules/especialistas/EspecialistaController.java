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
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@RestController
@RequestMapping("/api/especialistas")
@CrossOrigin(origins = "*")
@Slf4j
public class EspecialistaController {

    @Autowired
    private EspecialistaService especialistaService;

    @Autowired
    private StorageService storageService;

    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping()
    public List<EspecialistaDTO> listarEspecialistasActivos() {
        log.info("Petición GET para listar todos los especialistas activos");
        return especialistaService.listarEspecialistasActivos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<EspecialistaDTO> obtenerEspecialistaPorId(@PathVariable Integer id) {
        log.info("Petición GET para obtener especialista por ID: {}", id);
        EspecialistaDTO especialista = especialistaService.obtenerEspecialistaPorId(id);
        if (especialista != null) {
            return ResponseEntity.ok(especialista);
        }
        log.warn("Especialista ID {} no encontrado", id);
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/buscar")
    public List<EspecialistaDTO> buscarEspecialistas(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Integer especialidadId,
            @RequestParam(required = false) Integer sedeId) {
        log.info("Petición GET para buscar especialistas. Search: {}, EspecialidadID: {}, SedeID: {}", search, especialidadId, sedeId);
        return especialistaService.buscarEspecialistas(search, especialidadId, sedeId);
    }


    @GetMapping("/foto/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> obtenerFotoEspecialista(@PathVariable String filename) {
        log.debug("Solicitud para obtener foto: {}", filename);
        Resource file = storageService.loadAsResource(filename);
        if (file == null) {
            log.warn("Foto {} no encontrada", filename);
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getFilename() + "\"")
                .body(file);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> crearEspecialista(
            @RequestPart("data") String dataJson,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        log.info("Petición POST para crear especialista. Data: {}", dataJson);
        try {
            EspecialistaRequest request = objectMapper.readValue(dataJson, EspecialistaRequest.class);
            EspecialistaDTO created = especialistaService.crearEspecialista(request, file);
            return ResponseEntity.ok(created);
        } catch (JsonProcessingException e) {
            log.error("Error el parsear JSON en creación de especialista: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Error parsing JSON");
        } catch (Exception e) {
            log.error("Error al crear especialista: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Error creating especialista: " + e.getMessage());
        }
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> actualizarEspecialista(
            @PathVariable Integer id,
            @RequestPart("data") String dataJson,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        log.info("Petición PUT para actualizar especialista ID: {}", id);
        try {
            EspecialistaRequest request = objectMapper.readValue(dataJson, EspecialistaRequest.class);
            EspecialistaDTO updated = especialistaService.actualizarEspecialista(id, request, file);
            return ResponseEntity.ok(updated);
        } catch (JsonProcessingException e) {
            log.error("Error al parsear JSON en actualización: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Error parsing JSON");
        } catch (Exception e) {
            log.error("Error al actualizar especialista ID {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body("Error updating especialista: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarEspecialista(@PathVariable Integer id) {
        log.info("Petición DELETE para eliminar especialista ID: {}", id);
        especialistaService.eliminarEspecialista(id);
        return ResponseEntity.noContent().build();
    }
}

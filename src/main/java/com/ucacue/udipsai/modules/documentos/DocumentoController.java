package com.ucacue.udipsai.modules.documentos;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@RestController
@RequestMapping("/api/documentos")
@CrossOrigin(origins = "*")
public class DocumentoController {

    @Autowired
    private DocumentoService documentoService;

    @PostMapping
    public ResponseEntity<Documento> createDocumento(
            @RequestParam("file") MultipartFile file,
            @RequestParam("pacienteId") Long pacienteId,
            @RequestParam("nombre") String nombre ) {
        try {
            Documento savedDocumento = documentoService.saveDocumento(file, pacienteId, nombre);
            return ResponseEntity.ok(savedDocumento);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Documento> getDocumentoById(@PathVariable Long id) {
        Optional<Documento> documento = documentoService.getDocumentoById(id);
        return documento.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/descargar")
    @ResponseBody
    public ResponseEntity<Resource> descargarDocumento(@PathVariable Long id) {
        Resource file = documentoService.loadDocumentoAsResource(id);
        if (file == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                .body(file);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocumento(@PathVariable Long id) {
        documentoService.deleteDocumento(id);
        return ResponseEntity.noContent().build();
    }
}

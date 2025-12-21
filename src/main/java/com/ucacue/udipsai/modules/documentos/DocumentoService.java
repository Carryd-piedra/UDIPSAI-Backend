package com.ucacue.udipsai.modules.documentos;

import com.ucacue.udipsai.modules.paciente.Paciente;
import com.ucacue.udipsai.modules.paciente.PacienteRepositorio;
import com.ucacue.udipsai.modules.storage.StorageService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;

import java.util.List;
import java.util.Optional;

@Service
public class DocumentoService {

    @Autowired
    private DocumentoRepositorio documentoRepositorio;

    @Autowired
    private PacienteRepositorio pacienteRepositorio;

    @Autowired
    private StorageService storageService;

    public Optional<Documento> getDocumentoById(Long id) {
        return documentoRepositorio.findById(id);
    }

    public Documento saveDocumento(MultipartFile file, Long pacienteId, String nombre) {
        Paciente paciente = pacienteRepositorio.findById(pacienteId.intValue())
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado con ID: " + pacienteId));

        String filename = storageService.store(file);

        Documento documento = new Documento();
        documento.setUrl(filename);
        documento.setPaciente(paciente);
        documento.setActivo(true);
        Documento savedDoc = documentoRepositorio.save(documento);

        // Actualizar lista en Paciente (JSON field)
        DocumentoAdjunto docAdjunto = new DocumentoAdjunto(
                savedDoc.getId(),
                nombre != null ? nombre : file.getOriginalFilename(),
                filename
        );
        paciente.getDocumentosPaciente().add(docAdjunto);
        pacienteRepositorio.save(paciente);

        return savedDoc;
    }

    public Resource loadDocumentoAsResource(Long id) {
        Documento documento = documentoRepositorio.findById(id)
                .orElseThrow(() -> new RuntimeException("Documento no encontrado con ID: " + id));
        return storageService.loadAsResource(documento.getUrl());
    }

    @Transactional
    public void deleteDocumento(Long id) {
        Documento documento = documentoRepositorio.findById(id)
                .orElseThrow(() -> new RuntimeException("Documento no encontrado con ID: " + id));

        // Eliminar referencia en Paciente list
        Paciente paciente = documento.getPaciente();
        if (paciente != null) {
            List<DocumentoAdjunto> docs = paciente.getDocumentosPaciente();
            docs.removeIf(d -> d.getId() == id);
            paciente.setDocumentosPaciente(docs);
            pacienteRepositorio.save(paciente);
        }

        // Soft delete logic if needed, but for files maybe hard delete or just flag?
        // Entity has 'activo', let's use it.
        documento.setActivo(false);
        documentoRepositorio.save(documento);
        
        // Note: StorageService usually doesn't delete files to be safe, but can be added if needed.
    }
    
    // Legacy support method replacement if needed for direct byte access, 
    // but Controller should prefer Resource
    public String guardarArchivoEnDisco(byte[] content) {
        // This is tricky as StorageService expects MultipartFile.
        // If this is strictly needed by other services, we might need a store(byte[]) method in StorageService.
        // For now, assuming most uploads come as MultipartFile.
        return null; 
    }
}

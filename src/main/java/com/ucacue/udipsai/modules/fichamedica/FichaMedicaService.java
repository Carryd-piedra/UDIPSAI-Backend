package com.ucacue.udipsai.modules.fichamedica;

import com.ucacue.udipsai.modules.paciente.Paciente;
import com.ucacue.udipsai.modules.paciente.PacienteRepositorio;
import com.ucacue.udipsai.modules.paciente.PacienteService;
import com.ucacue.udipsai.modules.storage.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FichaMedicaService {

    @Autowired
    private FichaMedicaRepository fichaMedicaRepository;

    @Autowired
    private PacienteRepositorio pacienteRepositorio;

    @Autowired
    private PacienteService pacienteService;

    @Autowired
    private StorageService storageService;

    @Transactional(readOnly = true)
    public List<FichaMedicaDTO> listarFichasMedicas() {

        log.info("Consultando todas las fichas médicas activas");
        return fichaMedicaRepository.findAll().stream()
                .filter(FichaMedica::getActivo)
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public FichaMedicaDTO obtenerFichaMedicaPorPacienteId(Integer pacienteId) {

        log.info("Consultando ficha médica activa para el paciente ID: {}", pacienteId);
        FichaMedica ficha = fichaMedicaRepository.findByPacienteIdAndActivo(pacienteId, true);
        if (ficha != null && ficha.getActivo()) {
            return convertirADTO(ficha);
        }
        return null;
    }

    @Transactional(readOnly = true)
    public FichaMedica obtenerEntidadFichaPorIdPaciente(Integer pacienteId) {

        log.debug("Consultando entidad FichaMedica por Paciente ID: {}", pacienteId);
        return fichaMedicaRepository.findByPacienteIdAndActivo(pacienteId, true);
    }

    @Transactional
    public FichaMedicaDTO guardarFichaMedica(FichaMedicaRequest request, MultipartFile genogramaFile) {
        log.info("Iniciando guardado de ficha médica para Paciente ID: {}", request.getPacienteId());
        if (request.getPacienteId() == null) {
            log.error("El ID del paciente es requerido");
            throw new IllegalArgumentException("El ID del paciente es requerido");
        }
        FichaMedica ficha = fichaMedicaRepository.findByPacienteIdAndActivo(request.getPacienteId(), true);

        if (ficha == null) {
            log.info("Creando nueva ficha médica para Paciente ID: {}", request.getPacienteId());
            ficha = new FichaMedica();
            Paciente paciente = pacienteRepositorio.findById(request.getPacienteId())
                    .orElseThrow(() -> {
                        log.error("Error al guardar ficha: Paciente ID {} no encontrado", request.getPacienteId());
                        return new RuntimeException("Paciente no encontrado");
                    });
            ficha.setPaciente(paciente);
        } else {
            log.info("Actualizando ficha médica existente ID: {}", ficha.getId());
        }

        if (request.getDatosFamiliares() != null)
            ficha.setDatosFamiliares(request.getDatosFamiliares());
        if (request.getHistoriaPrenatal() != null)
            ficha.setHistoriaPrenatal(request.getHistoriaPrenatal());
        if (request.getHistoriaNatal() != null)
            ficha.setHistoriaNatal(request.getHistoriaNatal());
        if (request.getHistoriaPostnatal() != null)
            ficha.setHistoriaPostnatal(request.getHistoriaPostnatal());
        if (request.getDesarrolloMotor() != null)
            ficha.setDesarrolloMotor(request.getDesarrolloMotor());
        if (request.getAlimentacion() != null)
            ficha.setAlimentacion(request.getAlimentacion());
        if (request.getAntecedentesMedicos() != null)
            ficha.setAntecedentesMedicos(request.getAntecedentesMedicos());

        ficha.setActivo(true);

        if (genogramaFile != null && !genogramaFile.isEmpty()) {
            String filename = storageService.store(genogramaFile);
            log.info("Genograma almacenado: {}", filename);
            ficha.setGenogramaUrl(filename);
        }

        FichaMedica saved = fichaMedicaRepository.save(ficha);
        log.info("Ficha médica guardada exitosamente ID: {}", saved.getId());
        return convertirADTO(saved);
    }

    public Resource cargarGenogramaComoRecurso(Integer pacienteId) {
        log.info("Solicitando recurso genograma para paciente ID: {}", pacienteId);
        FichaMedica ficha = fichaMedicaRepository.findByPacienteIdAndActivo(pacienteId, true);
        if (ficha != null && ficha.getGenogramaUrl() != null) {
            return storageService.loadAsResource(ficha.getGenogramaUrl());
        }
        log.warn("Genograma no encontrado o URL nula para paciente ID: {}", pacienteId);
        return null;
    }

    @Transactional
    public void eliminarFichaMedica(Integer id) {

        if (id == null)
            return;
        log.info("Eliminando ficha médica ID: {}", id);
        fichaMedicaRepository.findById(id).ifPresent(f -> {
            f.setActivo(false);
            fichaMedicaRepository.save(f);
            log.info("Ficha médica ID: {} desactivada", id);
        });
    }

    private FichaMedicaDTO convertirADTO(FichaMedica ficha) {
        FichaMedicaDTO dto = new FichaMedicaDTO();
        dto.setId(ficha.getId());
        dto.setPaciente(pacienteService.convertirADTO(ficha.getPaciente()));
        dto.setActivo(ficha.getActivo());
        dto.setGenogramaUrl(ficha.getGenogramaUrl());

        dto.setDatosFamiliares(ficha.getDatosFamiliares());
        dto.setHistoriaPrenatal(ficha.getHistoriaPrenatal());
        dto.setHistoriaNatal(ficha.getHistoriaNatal());
        dto.setHistoriaPostnatal(ficha.getHistoriaPostnatal());
        dto.setDesarrolloMotor(ficha.getDesarrolloMotor());
        dto.setAlimentacion(ficha.getAlimentacion());
        dto.setAntecedentesMedicos(ficha.getAntecedentesMedicos());

        return dto;
    }
}

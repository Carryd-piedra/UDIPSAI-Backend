package com.ucacue.udipsai.modules.reportes;

import com.ucacue.udipsai.modules.fonoaudiologia.FonoaudiologiaService;
import com.ucacue.udipsai.modules.psicologiaclinica.PsicologiaClinicaService;
import com.ucacue.udipsai.modules.psicologiaeducativa.PsicologiaEducativaService;
import com.ucacue.udipsai.modules.fichamedica.FichaMedica;
import com.ucacue.udipsai.modules.fichamedica.FichaMedicaService;
import com.ucacue.udipsai.modules.fonoaudiologia.Fonoaudiologia;
import com.ucacue.udipsai.modules.paciente.Paciente;
import com.ucacue.udipsai.modules.psicologiaclinica.PsicologiaClinica;
import com.ucacue.udipsai.modules.psicologiaeducativa.PsicologiaEducativa;
import com.ucacue.udipsai.modules.storage.StorageService;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReporteGeneralService {

    @Autowired
    private FichaMedicaService fichaMedicaService;

    @Autowired
    private PsicologiaEducativaService psicologiaEducativaService;

    @Autowired
    private PsicologiaClinicaService psicologiaClinicaService;

    @Autowired
    FonoaudiologiaService fonoaudiologiaService;

    @Autowired
    private StorageService storageService;

    public byte[] generarReporteGeneralPorPacienteId(Integer idPaciente) {
        try {
            FichaMedica fichaMedica = fichaMedicaService.obtenerFichaPorIdPaciente(idPaciente);
            if (fichaMedica == null) throw new RuntimeException("Ficha Médica no encontrada");

            PsicologiaEducativa psicologiaEducativa = psicologiaEducativaService.obtenerPorIdPaciente(idPaciente);
            if (psicologiaEducativa == null) throw new RuntimeException("Psicología Educativa no encontrada");

            PsicologiaClinica psicologiaClinica = psicologiaClinicaService.obtenerFichaPorIdPaciente(idPaciente);
            if(psicologiaClinica == null) throw new RuntimeException("Psicologia Clínica no encontrada");

            Fonoaudiologia fonoaudiologia = fonoaudiologiaService.obtenerPorIdPaciente(idPaciente);
            if(fonoaudiologia == null) throw new RuntimeException("Fonoaudiología no encontrada");

            Paciente paciente = fichaMedica.getPaciente();

            // Image Handling: Paciente Image
            InputStream imagenPacienteStream = null;
            if (paciente.getFotoUrl() != null && !paciente.getFotoUrl().isEmpty()) {
                try {
                    Resource imageRes = storageService.loadAsResource(paciente.getFotoUrl());
                    if (imageRes.exists() && imageRes.isReadable()) {
                        imagenPacienteStream = imageRes.getInputStream();
                    }
                } catch (Exception e) {
                    System.err.println("Error loading patient image: " + e.getMessage());
                }
            }

            // Image Handling: Genograma
            InputStream genogramaStream = null;
            if (fichaMedica.getGenogramaUrl() != null && !fichaMedica.getGenogramaUrl().isEmpty()) {
                try {
                    Resource genoRes = storageService.loadAsResource(fichaMedica.getGenogramaUrl());
                    if (genoRes.exists() && genoRes.isReadable()) {
                        genogramaStream = genoRes.getInputStream();
                    }
                } catch (Exception e) {
                    System.err.println("Error loading genograma: " + e.getMessage());
                }
            }
            
            // Buffer streams for reuse
            byte[] pacienteImageBytes = null;
            if (imagenPacienteStream != null) {
                 pacienteImageBytes = imagenPacienteStream.readAllBytes();
                 imagenPacienteStream.close();
            }
            
            byte[] genogramaBytes = null;
            if (genogramaStream != null) {
                genogramaBytes = genogramaStream.readAllBytes();
                genogramaStream.close();
            }
            
            String backgroundImage = "https://upload.wikimedia.org/wikipedia/commons/6/6c/Logo_Universidad_Cat%C3%B3lica_de_Cuenca.jpg";

            // Ficha Medica Params
            Map<String, Object> parametrosFichaMedica = new HashMap<>();
            parametrosFichaMedica.put("backgroundImage", backgroundImage);
            parametrosFichaMedica.put("imagenPaciente", pacienteImageBytes != null ? new ByteArrayInputStream(pacienteImageBytes) : null);
            parametrosFichaMedica.put("genogramaFamiliar", genogramaBytes != null ? new ByteArrayInputStream(genogramaBytes) : null);

            // Psicologia Educativa Params
            Map<String, Object> parametrosPsicologiaEducativa = new HashMap<>();
            parametrosPsicologiaEducativa.put("backgroundImage", backgroundImage);
            parametrosPsicologiaEducativa.put("imagenPaciente", pacienteImageBytes != null ? new ByteArrayInputStream(pacienteImageBytes) : null);

            // Psicologia Clinica Params
            Map<String, Object> parametrosPsicologiaClinica = new HashMap<>();
            parametrosPsicologiaClinica.put("backgroundImage", backgroundImage);
            parametrosPsicologiaClinica.put("imagenPaciente", pacienteImageBytes != null ? new ByteArrayInputStream(pacienteImageBytes) : null);

            // Fonoaudiologia Params
            Map<String, Object> parametrosFonoaudiologia = new HashMap<>();
            parametrosFonoaudiologia.put("backgroundImage", backgroundImage);
            parametrosFonoaudiologia.put("imagenPaciente", pacienteImageBytes != null ? new ByteArrayInputStream(pacienteImageBytes) : null);

            // Load Templates
            ClassPathResource resourceFichaMedica = new ClassPathResource("reportes/ficha_medica.jrxml");
            JasperReport reporteFichaMedica = JasperCompileManager.compileReport(resourceFichaMedica.getInputStream());

            ClassPathResource resourcePsicologiaEducativa = new ClassPathResource("reportes/psicologia_educativa.jrxml");
            JasperReport reportePsicologiaEducativa = JasperCompileManager.compileReport(resourcePsicologiaEducativa.getInputStream());

            ClassPathResource resourcePsicologiaClinica = new ClassPathResource("reportes/psicologia_clinica.jrxml");
            JasperReport reportePsicologiaClinica = JasperCompileManager.compileReport(resourcePsicologiaClinica.getInputStream());

            ClassPathResource resourceFonoaudiologia = new ClassPathResource("reportes/fonoaudiologia.jrxml");
            JasperReport reporteFonoaudiologia = JasperCompileManager.compileReport(resourceFonoaudiologia.getInputStream());

            // Create Data Sources
            JRBeanCollectionDataSource dataSourceFichaMedica = new JRBeanCollectionDataSource(Collections.singletonList(fichaMedica));
            JRBeanCollectionDataSource dataSourcePsicologiaEducativa = new JRBeanCollectionDataSource(Collections.singletonList(psicologiaEducativa));
            JRBeanCollectionDataSource dataSourcePsicologiaClinica = new JRBeanCollectionDataSource(Collections.singletonList(psicologiaClinica));
            JRBeanCollectionDataSource dataSourceFonoaudiologia = new JRBeanCollectionDataSource(Collections.singletonList(fonoaudiologia));

            // Fill Reports
            JasperPrint printFichaMedica = JasperFillManager.fillReport(reporteFichaMedica, parametrosFichaMedica, dataSourceFichaMedica);
            JasperPrint printPsicologiaEducativa = JasperFillManager.fillReport(reportePsicologiaEducativa, parametrosPsicologiaEducativa, dataSourcePsicologiaEducativa);
            JasperPrint printPsicologiaClinica = JasperFillManager.fillReport(reportePsicologiaClinica, parametrosPsicologiaClinica, dataSourcePsicologiaClinica);
            JasperPrint printFonoaudiologia = JasperFillManager.fillReport(reporteFonoaudiologia, parametrosFonoaudiologia, dataSourceFonoaudiologia);

            // Merge Reports
            List<JasperPrint> reportesAUnir = new ArrayList<>();
            reportesAUnir.add(printFichaMedica);
            reportesAUnir.add(printPsicologiaEducativa);
            reportesAUnir.add(printPsicologiaClinica);
            reportesAUnir.add(printFonoaudiologia);

            JasperPrint reporteGeneral = reportesAUnir.get(0);
            for (int i = 1; i < reportesAUnir.size(); i++) {
                reporteGeneral.getPages().addAll(reportesAUnir.get(i).getPages());
            }

            return JasperExportManager.exportReportToPdf(reporteGeneral);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al generar el reporte general: " + e.getMessage());
        }
    }
}

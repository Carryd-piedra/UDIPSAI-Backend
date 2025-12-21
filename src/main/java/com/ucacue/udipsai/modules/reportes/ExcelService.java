package com.ucacue.udipsai.modules.reportes;

import com.ucacue.udipsai.modules.instituciones.InstitucionEducativa;
import com.ucacue.udipsai.modules.paciente.Paciente;
import com.ucacue.udipsai.modules.paciente.Paciente.JornadaEnum;
import com.ucacue.udipsai.modules.sedes.Sede;
import com.ucacue.udipsai.modules.instituciones.InstitucionEducativaRepositorio;
import com.ucacue.udipsai.modules.paciente.PacienteRepositorio;
import com.ucacue.udipsai.modules.sedes.SedeRepositorio;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.time.ZoneId;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class ExcelService {

    @Autowired
    private PacienteRepositorio pacienteRepository;

    @Autowired
    private InstitucionEducativaRepositorio institucionEducativaRepositorio;

    // JornadaRepositorio removed

    @Autowired
    private SedeRepositorio sedeRepositorio;

    @Transactional
    public List<String> savePatientsFromExcel(MultipartFile file) throws IOException {
        List<Paciente> pacientes = new ArrayList<>();
        List<String> messages = new ArrayList<>();
        List<String> warnings = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        Workbook workbook;
        try {
            workbook = new XSSFWorkbook(file.getInputStream());
        } catch (Exception e) {
            messages.add("Error al leer el archivo Excel. Asegúrese de que el archivo sea válido.");
            return messages;
        }

        Sheet sheet = workbook.getSheetAt(0);

        int rowNum = 0;

        for (Row row : sheet) {
            rowNum++;
            if (row.getRowNum() == 0) {
                continue;
            }

            Paciente paciente = new Paciente();

            try {
                // Procesar ID
                Cell idCell = row.getCell(0);
                if (idCell == null || idCell.getCellType() == CellType.BLANK) {
                    paciente.setId(null);
                } else {
                    int id = (int) getCellValueAsNumeric(idCell);
                    if (pacienteRepository.findById(id).isPresent()) {
                        warnings.add("Fila " + rowNum + ": ID " + id + " ya existe. Se asignará un nuevo ID.");
                        paciente.setId(null);
                    } else {
                        paciente.setId(id);
                    }
                }

                // PacienteEstado is Integer? Assuming 1 is active. 
                // In Entity it might be Boolean 'activo'. 
                // Let's check Paciente entity fields... 
                // Assuming paciente.setActivo(true) is better if field changed. 
                // But let's stick to what we saw in other files (setActivo(true)).
                paciente.setActivo(true);

                // Cédula
                String cedula = getCellValueAsString(row.getCell(6));
                if (cedula == null || cedula.trim().isEmpty()) {
                    warnings.add("Fila " + rowNum + ": La cédula está vacía. Se guardará como null.");
                    paciente.setCedula(null);
                } else {
                    paciente.setCedula(cedula);
                }

                // Dates: Convert java.util.Date to LocalDateTime/LocalDate
                Date fechaApertura = getCellValueAsDate(row.getCell(1), dateFormat);
                if (fechaApertura != null) {
                    paciente.setFechaApertura(fechaApertura.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
                }
                
                paciente.setNombresApellidos(getCellValueAsString(row.getCell(2)));
                paciente.setCiudad(getCellValueAsString(row.getCell(3)));
                
                Date fechaNacimiento = getCellValueAsDate(row.getCell(4), dateFormat);
                if (fechaNacimiento != null) {
                    paciente.setFechaNacimiento(fechaNacimiento.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
                }

                paciente.setDomicilio(getCellValueAsString(row.getCell(7)));
                paciente.setNumeroTelefono(getCellValueAsString(row.getCell(8)));
                paciente.setNumeroCelular(getCellValueAsString(row.getCell(9)));

                // Institución
                String institucionNombre = getCellValueAsString(row.getCell(10));
                String tipoInstitucion = getCellValueAsString(row.getCell(11));
                InstitucionEducativa institucion = getOrCreateInstitucionEducativa(institucionNombre, null, tipoInstitucion);
                paciente.setInstitucionEducativa(institucion);

                // Jornada (ENUM)
                String jornadaNombre = getCellValueAsString(row.getCell(12));
                JornadaEnum jornada = parseJornada(jornadaNombre, warnings, rowNum);
                paciente.setJornada(jornada);

                // Sede
                String sedeNombre = getCellValueAsString(row.getCell(20));
                Sede sede = getOrCreateSede(sedeNombre);
                paciente.setSede(sede);

                // Verificar duplicados (Use existsByCedula or findByCedula)
                if (paciente.getCedula() != null && pacienteRepository.existsByCedula(paciente.getCedula())) {
                    warnings.add("Fila " + rowNum + ": Paciente con cédula " + paciente.getCedula() + " ya está registrado.");
                }

                pacientes.add(paciente);

            } catch (Exception e) {
                messages.add("Fila " + rowNum + ": Error al procesar el paciente. " + e.getMessage());
                e.printStackTrace();
                continue;
            }
        }

        workbook.close();

        if (!messages.isEmpty()) {
            messages.add(0, "No se guardó ningún dato debido a errores en el archivo:");
            messages.addAll(warnings);
            return messages;
        }

        pacienteRepository.saveAll(pacientes);
        messages.add("Se han guardado " + pacientes.size() + " pacientes correctamente.");
        messages.addAll(warnings);

        return messages;
    }

    private JornadaEnum parseJornada(String nombre, List<String> warnings, int rowNum) {
        if (nombre == null || nombre.trim().isEmpty()) return null;
        try {
            return JornadaEnum.valueOf(nombre.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
             warnings.add("Fila " + rowNum + ": Jornada " + nombre + " inválida.");
             return null;
        }
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null || cell.getCellType() == CellType.BLANK) {
            return null;
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                DataFormatter formatter = new DataFormatter();
                return formatter.formatCellValue(cell);
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                switch (cell.getCachedFormulaResultType()) {
                    case NUMERIC:
                        DataFormatter formulaFormatter = new DataFormatter();
                        return formulaFormatter.formatCellValue(cell);
                    case STRING:
                        return cell.getStringCellValue();
                    default:
                        return null;
                }
            default:
                return null;
        }
    }

    private double getCellValueAsNumeric(Cell cell) {
        if (cell == null) {
            return 0;
        }
        if (cell.getCellType() == CellType.NUMERIC) {
            return cell.getNumericCellValue();
        } else if (cell.getCellType() == CellType.STRING) {
             try {
                return Double.parseDouble(cell.getStringCellValue());
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return 0;
    }

    private Date getCellValueAsDate(Cell cell, SimpleDateFormat dateFormat) {
        if (cell == null || cell.getCellType() == CellType.BLANK) {
            return null;
        }
        if (cell.getCellType() == CellType.NUMERIC) {
            if (DateUtil.isCellDateFormatted(cell)) {
                return cell.getDateCellValue();
            } else {
                return DateUtil.getJavaDate(cell.getNumericCellValue());
            }
        } else if (cell.getCellType() == CellType.STRING) {
            try {
                return dateFormat.parse(cell.getStringCellValue());
            } catch (ParseException e) {
                return null;
            }
        } else {
            return null;
        }
    }

    private InstitucionEducativa getOrCreateInstitucionEducativa(String nombre, String direccion, String tipoInstitucion) {
        if (nombre == null || nombre.isEmpty()) {
            return null;
        }
        Optional<InstitucionEducativa> optional = institucionEducativaRepositorio.findByNombreIgnoreCase(nombre);
        if (optional.isPresent()) {
            return optional.get();
        } else {
            InstitucionEducativa nuevaInstitucion = new InstitucionEducativa();
            nuevaInstitucion.setNombre(nombre);
            nuevaInstitucion.setDireccion(direccion);
            nuevaInstitucion.setTipo(tipoInstitucion);
            nuevaInstitucion.setActivo(true);
            return institucionEducativaRepositorio.save(nuevaInstitucion);
        }
    }

    private Sede getOrCreateSede(String nombre) {
        if (nombre == null || nombre.isEmpty()) {
            return null;
        }
        List<Sede> resultados = sedeRepositorio.findByNombreIgnoreCase(nombre);
        if (resultados.isEmpty()) {
            Sede nuevaSede = new Sede();
            nuevaSede.setNombre(nombre);
            nuevaSede.setActivo(true);
            return sedeRepositorio.save(nuevaSede);
        } else {
            return resultados.get(0);
        }
    }
}

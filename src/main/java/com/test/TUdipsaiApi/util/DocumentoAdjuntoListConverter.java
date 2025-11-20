package com.test.TUdipsaiApi.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.aspectj.apache.bcel.generic.Type;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.TUdipsaiApi.Model.DocumentoAdjunto;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class DocumentoAdjuntoListConverter implements AttributeConverter<List<DocumentoAdjunto>, String>{

    private static final ObjectMapper objectMapper= new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<DocumentoAdjunto> lista) {
        try{
            return objectMapper.writeValueAsString(lista);

        }catch(JsonProcessingException ex){
            throw new RuntimeException("Error al convertir a json", ex);
        }
    }

    @Override
    public List<DocumentoAdjunto> convertToEntityAttribute(String json) {
        try {
            if (json == null || json.isEmpty()) return new ArrayList<>();
            return objectMapper.readValue(json, new TypeReference<List<DocumentoAdjunto>>() {});
        } catch (IOException e) {
            System.err.println("JSON inválido: " + json); // Agrega esta línea para depurar
            throw new RuntimeException("Error al convertir json a lista", e);
        }
    }
    

}

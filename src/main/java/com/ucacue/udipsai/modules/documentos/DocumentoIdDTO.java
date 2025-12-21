package com.ucacue.udipsai.modules.documentos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DocumentoIdDTO {
    private Long id;

    public DocumentoIdDTO(Long id) {
        this.id = id;
    }

    public DocumentoIdDTO() {
    }
}

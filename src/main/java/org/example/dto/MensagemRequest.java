package org.example.dto;

import lombok.*;

@Builder
public class MensagemRequest {
    @Getter
    private String usuario;
    @Getter
    private String conteudo;
}

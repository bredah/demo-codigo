package org.example.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Jacksonized
public class Mensagem {

    @Id
    @GenericGenerator(name = "uuid")
    private UUID id;

    @NotEmpty(message = "usuário não pode estar vazio")
    private String usuario;

    @NotEmpty(message = "conteúdo não pode estar vazio")
    private String conteudo;

    @Default
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSS")
    private LocalDateTime dataCriacao = LocalDateTime.now();

    @Default
    private int gostei = 0;

}


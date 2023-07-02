package org.example.repository;

import jakarta.transaction.Transactional;
import org.example.model.Mensagem;
import org.example.utils.DisplayTestName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
@DisplayNameGeneration(DisplayTestName.class)
class MensagemRepositoryIT {

    @Autowired
    private MensagemRepository mensagemRepository;

    @Test
    void devePermitirCriarTabela() {
        long totalTabelasCriada = mensagemRepository.count();
        assertThat(totalTabelasCriada).isNotNegative();
    }

    @Test
    void devePermitirRegistrarMensagem() {
        // Arrange
        var mensagem = Mensagem.builder()
                .id(UUID.randomUUID())
                .usuario("joe")
                .conteudo("xpto test")
                .build();
        // Act
        var mensagemArmazenada = mensagemRepository.save(mensagem);
        // Assert
        assertThat(mensagemArmazenada).isInstanceOf(Mensagem.class);
        assertThat(mensagemArmazenada).isNotNull();
        assertThat(mensagemArmazenada).isEqualTo(mensagem);
        assertThat(mensagemArmazenada)
                .extracting(Mensagem::getId)
                .isEqualTo(mensagem.getId());
        assertThat(mensagemArmazenada)
                .extracting(Mensagem::getUsuario)
                .isEqualTo(mensagem.getUsuario());
        assertThat(mensagemArmazenada)
                .extracting(Mensagem::getConteudo)
                .isEqualTo(mensagem.getConteudo());
        assertThat(mensagemArmazenada)
                .extracting(Mensagem::getDataCriacao)
                .isEqualTo(mensagem.getDataCriacao());
    }

    @Test
    void devePermitirConsultarMensagem() {
        // Arrange
        var mensagem = registrarMensagem();
        var id = mensagem.getId();
        // Act
        var mensagemOptional = mensagemRepository.findById(id);
        // Assert
        assertThat(mensagemOptional)
                .isPresent()
                .containsSame(mensagem);
        mensagemOptional.ifPresent(mensagemArmazenada -> {
            assertThat(mensagemArmazenada.getId())
                    .isEqualTo(mensagem.getId());
            assertThat(mensagemArmazenada.getUsuario())
                    .isEqualTo(mensagem.getUsuario());
            assertThat(mensagemArmazenada.getConteudo())
                    .isEqualTo(mensagem.getConteudo());
            assertThat(mensagemArmazenada.getDataCriacao())
                    .isEqualTo(mensagem.getDataCriacao());
        });
    }

    @Test
    void devePermitirApagarMensagem() {
        // Arrange
        var mensagem = registrarMensagem();
        var id = mensagem.getId();
        // Act
        mensagemRepository.deleteById(id);
        var mensagemOptional = mensagemRepository.findById(id);
        // Assert
        assertThat(mensagemOptional)
                .isEmpty();
    }

    @Test
    void devePermitirListarMensagens() {
        // Arrange
        var mensagem1 = registrarMensagem();
        var mensagem2 = registrarMensagem();
        var mensagemList = Arrays.asList(mensagem1, mensagem2);
        // Act
        var resultado = mensagemRepository.findAll();
        // Assert
        assertThat(resultado)
                .hasSize(2)
                .containsExactlyInAnyOrder(mensagem1, mensagem2);
    }

    private Mensagem gerarMensagem() {
        return Mensagem.builder()
                .usuario("joe")
                .conteudo("xpto test")
                .build();
    }

    private Mensagem registrarMensagem() {
        var mensagem = gerarMensagem();
        mensagem.setId(UUID.randomUUID());
        return mensagemRepository.save(mensagem);
    }

}

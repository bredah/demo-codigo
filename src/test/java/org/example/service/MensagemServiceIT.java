package org.example.service;

import org.example.utils.MensagemHelper;
import jakarta.transaction.Transactional;
import org.example.exception.MensagemNotFoundException;
import org.example.utils.DisplayTestName;
import org.example.model.Mensagem;
import org.example.repository.MensagemRepository;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
@DisplayNameGeneration(DisplayTestName.class)
class MensagemServiceIT {

    @Autowired
    private MensagemRepository mensagemRepository;

    @Autowired
    private MensagemService mensagemService;

    @Nested
    class RegistrarMensagem {
        @Test
        void devePermitirRegistrarMensagem() {
            var mensagem = MensagemHelper.gerarMensagem();

            var mensagemArmazenada = mensagemService.criarMensagem(mensagem);

            assertThat(mensagemArmazenada)
                    .isInstanceOf(Mensagem.class)
                    .isNotNull();
            assertThat(mensagemArmazenada.getUsuario())
                    .isNotNull()
                    .isNotEmpty()
                    .isEqualTo(mensagem.getUsuario());
            assertThat(mensagemArmazenada.getId())
                    .isNotNull();
            assertThat(mensagemArmazenada.getConteudo())
                    .isNotNull()
                    .isNotEmpty()
                    .isEqualTo(mensagem.getConteudo());
        }
    }


    @Nested
    class BuscarMensagem {
        @Test
        void devePermitirBuscarMensagem() {
            var mensagem = MensagemHelper.registrarMensagem(mensagemRepository);

            var mensagemObtidaOptional = mensagemRepository.findById(mensagem.getId());

            assertThat(mensagemObtidaOptional)
                    .isPresent()
                    .containsSame(mensagem);
            mensagemObtidaOptional.ifPresent(mensagemObtida -> {
                assertThat(mensagemObtida.getId())
                        .isEqualTo(mensagem.getId());
                assertThat(mensagemObtida.getUsuario())
                        .isEqualTo(mensagem.getUsuario());
                assertThat(mensagemObtida.getConteudo())
                        .isEqualTo(mensagem.getConteudo());
                assertThat(mensagemObtida.getDataCriacao())
                        .isEqualTo(mensagem.getDataCriacao());
            });
        }

        @Test
        void deveGerarExcecao_QuandoBuscarMensagem_IdNaoExistente() {
            var id = UUID.randomUUID();

            assertThatThrownBy(() -> mensagemService.buscarMensagem(id))
                    .isInstanceOf(MensagemNotFoundException.class)
                    .hasMessage("Mensagem não encontrada");
        }
    }

    @Nested
    class AlterarMensagem {

        @Test
        void devePermirirAlterarMensagem() {
            var mensagemOriginal = MensagemHelper.registrarMensagem(mensagemRepository);
            var mensagemModificada = mensagemOriginal;
            mensagemModificada.setConteudo("abcd");

            var mensagemObtida = mensagemService.alterarMensagem(mensagemOriginal.getId(), mensagemModificada);

            assertThat(mensagemObtida)
                    .isInstanceOf(Mensagem.class)
                    .isNotNull();
            assertThat(mensagemObtida.getId())
                    .isEqualTo(mensagemModificada.getId());
            assertThat(mensagemObtida.getUsuario())
                    .isEqualTo(mensagemModificada.getUsuario());
            assertThat(mensagemObtida.getConteudo())
                    .isEqualTo(mensagemModificada.getConteudo());
        }

        @Test
        void deveGerarExcecao_QuandoAlterarMensagem_IdNaoExistente() {
            var id = UUID.randomUUID();
            var mensagemOriginal = MensagemHelper.gerarMensagem();
            mensagemOriginal.setId(id);

            assertThatThrownBy(() -> mensagemService.alterarMensagem(id, mensagemOriginal))
                    .isInstanceOf(MensagemNotFoundException.class)
                    .hasMessage("Mensagem não encontrada");
        }

        @Test
        void deveGerarExcecao_QuandoAlterarMensagem_IdNaoCoincide() {
            var mensagemOriginal = MensagemHelper.registrarMensagem(mensagemRepository);
            var mensagemModificada = MensagemHelper.registrarMensagem(mensagemRepository);

            assertThatThrownBy(
                    () -> mensagemService.alterarMensagem(mensagemOriginal.getId(), mensagemModificada))
                    .isInstanceOf(MensagemNotFoundException.class)
                    .hasMessage("Mensagem não apresenta o ID correto");
        }

    }

    @Nested
    class ApagarMensagem {

        @Test
        void devePermitirApagarMensagem() {
            var mensagemRegistrada = MensagemHelper.registrarMensagem(mensagemRepository);
            mensagemService.apagarMensagem(mensagemRegistrada.getId());
        }

        @Test
        void deveGerarExcecao_QuandoApagarMensagem_IdNaoExistente() {
            var id = UUID.randomUUID();
            assertThatThrownBy(() -> mensagemService.apagarMensagem(id))
                    .isInstanceOf(MensagemNotFoundException.class)
                    .hasMessage("Mensagem não encontrada");
        }

    }

    @Nested
    class IncrementarGostei {

        @Test
        void devePermitirIncrementarGostei() {
            var mensagemRegistrada = MensagemHelper.registrarMensagem(mensagemRepository);

            var mensagemRecebida = mensagemService.incrementarGostei(mensagemRegistrada.getId());

            assertThat(mensagemRecebida.getGostei()).isEqualTo(1);
        }

        @Test
        void deveGerarExcecao_QuandoIncrementarGostei_IdNaoExistente() {
            var id = UUID.randomUUID();

            assertThatThrownBy(() -> mensagemService.incrementarGostei(id))
                    .isInstanceOf(MensagemNotFoundException.class)
                    .hasMessage("Mensagem não encontrada");
        }

    }

    @Nested
    class ListarMensagens {

        @Test
        void devePermitirListarMensagens() {
            MensagemHelper.registrarMensagem(mensagemRepository);
            MensagemHelper.registrarMensagem(mensagemRepository);

            Page<Mensagem> mensagens = mensagemService.listarMensagens(Pageable.unpaged());

            assertThat(mensagens).hasSize(2);
            assertThat(mensagens.getContent())
                    .asList()
                    .allSatisfy(mensagem -> {
                        assertThat(mensagem).isNotNull();
                        assertThat(mensagem).isInstanceOf(Mensagem.class);
                    });
        }

        @Test
        void devePermitirListarTodasAsMensagens_QuandoNaoExisteRegistro() {
            Page<Mensagem> mensagens = mensagemService.listarMensagens(Pageable.unpaged());

            assertThat(mensagens).isEmpty();
        }

    }
}

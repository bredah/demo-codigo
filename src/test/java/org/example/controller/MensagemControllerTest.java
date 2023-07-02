package org.example.controller;

import jakarta.validation.ConstraintViolationException;
import org.example.model.Mensagem;
import org.example.service.MensagemService;
import org.example.utils.DisplayTestName;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.http.HttpStatus;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(OutputCaptureExtension.class)
@DisplayNameGeneration(DisplayTestName.class)
class MensagemControllerTest {

    private MensagemController mensagemController;
    @Mock
    private MensagemService mensagemService;
    AutoCloseable openMocks;

    @BeforeEach
    void setUp() {
        openMocks = MockitoAnnotations.openMocks(this);
        mensagemController = new MensagemController(mensagemService);
    }

    @AfterEach
    void tearDown() throws Exception {
        openMocks.close();
    }

    @Nested
    class RegistrarMensagem {

        @Test
        void devePermitirRegistrarMensagem(CapturedOutput output) {
            var mensagemRequest = Mensagem.builder()
                    .usuario("John")
                    .conteudo("xpto")
                    .build();
            when(mensagemService.criarMensagem(any(Mensagem.class))).thenAnswer(i -> i.getArgument(0));

            var resposta = mensagemController.registrarMensagem(mensagemRequest);

            assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(resposta.getBody().getUsuario()).isEqualTo(mensagemRequest.getUsuario());
            assertThat(resposta.getBody().getConteudo()).isEqualTo(mensagemRequest.getConteudo());
            assertThat(output).contains("requisição para registrar mensagem foi efetuada");
            verify(mensagemService, times(1)).criarMensagem(any(Mensagem.class));
        }

        @Test
        void deveGerarExcecao_QuandoRegistrarMensagem_UsuarioEmBranco(CapturedOutput output) {
            var mensagemRequest = Mensagem.builder()
                    .usuario("")
                    .conteudo("xpto")
                    .build();

            assertThatThrownBy(() -> mensagemController.registrarMensagem(mensagemRequest))
                    .isInstanceOf(ConstraintViolationException.class);

        }
    }

    @Nested
    class BuscarMensagem {

        @Test
        void devePermitirBuscarMensagem(CapturedOutput output) {
            var id = UUID.randomUUID();
            var mensagem = Mensagem.builder()
                    .usuario("John")
                    .conteudo("xpto")
                    .build();
            when(mensagemService.buscarMensagem(any(UUID.class))).thenReturn(mensagem);

            var resposta = mensagemController.buscarMensagem(id);

            assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(resposta.getBody()).isNotNull().isEqualTo(mensagem);
            assertThat(output).contains("requisição para buscar mensagem foi efetuada");
            verify(mensagemService, times(1)).buscarMensagem(any(UUID.class));
        }

        @Test
        void deveGerarExcecao_QuandoBuscarMensagem_IdNaoExistente() {
            var id = UUID.randomUUID();
            var mensagem = Mensagem.builder()
                    .usuario("John")
                    .conteudo("xpto")
                    .build();
            when(mensagemService.buscarMensagem(any(UUID.class))).thenReturn(mensagem);

            var resposta = mensagemController.buscarMensagem(id);

            assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(resposta.getBody()).isNotNull().isEqualTo(mensagem);
        }

    }

    @Nested
    class AtualizarMensagem {

        @Test
        void devePermitirAtualizarMensagem(CapturedOutput output) {
            var id = UUID.randomUUID();
            var mensagem = Mensagem.builder()
                    .usuario("John")
                    .conteudo("xpto")
                    .build();
            when(mensagemService.alterarMensagem(any(UUID.class), any(Mensagem.class)))
                    .thenReturn(mensagem);

            var resposta = mensagemController.atualizarMensagem(id, mensagem);

            assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(resposta.getBody()).isNotNull().isEqualTo(mensagem);
            assertThat(output).contains("requisição para atualizar mensagem foi efetuada");
            verify(mensagemService, times(1))
                    .alterarMensagem(any(UUID.class), any(Mensagem.class));
        }
    }

    @Nested
    class ApagarMensagem {

        @Test
        void devePermitirApagarMensagem(CapturedOutput output) {
            var id = UUID.randomUUID();
            doNothing().when(mensagemService).apagarMensagem(any(UUID.class));

            var resposta = mensagemController.apagarMensagem(id);

            assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(output).contains("requisição para apagar mensagem foi efetuada");
            verify(mensagemService, times(1)).apagarMensagem(any(UUID.class));
        }
    }

}

package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.MensagemRequest;
import org.example.exception.MensagemNotFoundException;
import org.example.handler.GlobalExceptionHandler;
import org.example.model.Mensagem;
import org.example.service.MensagemService;
import org.example.utils.DisplayTestName;
import org.example.utils.MensagemHelper;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(OutputCaptureExtension.class)
@DisplayNameGeneration(DisplayTestName.class)
class MensagemControllerTest {

    private MockMvc mockMvc;

    @Mock
    private MensagemService mensagemService;

    private MensagemController mensagemController;

    AutoCloseable openMocks;

    @BeforeEach
    void setUp() {
        openMocks = MockitoAnnotations.openMocks(this);
        mensagemController = new MensagemController(mensagemService);
        mockMvc = MockMvcBuilders.standaloneSetup(mensagemController)
                .setControllerAdvice(new GlobalExceptionHandler()).build();
    }

    @AfterEach
    void tearDown() throws Exception {
        openMocks.close();
    }

    @Nested
    class RegistrarMensagem {

        @Test
        void devePermitirRegistrarMensagem(CapturedOutput output) throws Exception {
            var mensagemRequest = MensagemRequest.builder()
                    .usuario("Jose")
                    .conteudo("xpto")
                    .build();

            when(mensagemService.criarMensagem(any(Mensagem.class))).thenAnswer(i -> i.getArgument(0));

            mockMvc.perform(post("/mensagens")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(mensagemRequest)))
//                    .andDo(print())
                    .andExpect(status().isCreated());
            verify(mensagemService, times(1)).criarMensagem(any(Mensagem.class));
            assertThat(output).contains("requisição para registrar mensagem foi efetuada");
        }

        @Test
        void deveGerarExcecao_QuandoRegistrarMensagem_UsuarioEmBranco() throws Exception {
            var mensagemRequest = MensagemRequest.builder()
                    .usuario("")
                    .conteudo("xpto")
                    .build();

            mockMvc.perform(post("/mensagens")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(mensagemRequest)))
//                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Validation error"))
                    .andExpect(jsonPath("$.errors.[0]").value("usuário não pode estar vazio"));
            verify(mensagemService, never()).criarMensagem(any(Mensagem.class));
        }

        @Test
        void deveGerarExcecao_QuandoRegistrarMensagem_ConteudoEmBranco() throws Exception {
            var mensagemRequest = MensagemRequest.builder()
                    .usuario("John")
                    .conteudo("")
                    .build();

            mockMvc.perform(post("/mensagens")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(mensagemRequest)))
//                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Validation error"))
                    .andExpect(jsonPath("$.errors.[0]").value("conteúdo não pode estar vazio"));
            verify(mensagemService, never()).criarMensagem(any(Mensagem.class));
        }
    }

    @Nested
    class BuscarMensagem {
        @Test
        void devePermitirBuscarMensagem(CapturedOutput output) throws Exception {
            var id = UUID.randomUUID();
            var mensagem = MensagemHelper.gerarMensagem();
            mensagem.setId(id);
            mensagem.setDataCriacao(LocalDateTime.now());

            when(mensagemService.buscarMensagem(any(UUID.class))).thenReturn(mensagem);

            mockMvc.perform(get("/mensagens/{id}", id)
                            .contentType(MediaType.APPLICATION_JSON))
//                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(mensagem.getId().toString()))
                    .andExpect(jsonPath("$.conteudo").value(mensagem.getConteudo()))
                    .andExpect(jsonPath("$.usuario").value(mensagem.getUsuario()))
                    .andExpect(jsonPath("$.dataCriacao").exists())
                    .andExpect(jsonPath("$.gostei").exists());
            assertThat(output).contains("requisição para buscar mensagem foi efetuada");
            verify(mensagemService, times(1)).buscarMensagem(any(UUID.class));
        }

        @Test
        void deveGerarExcecao_QuandoBuscarMensagem_IdNaoExistente(CapturedOutput output) throws Exception {
            var id = UUID.randomUUID();

            when(mensagemService.buscarMensagem(any(UUID.class)))
                    .thenThrow(new MensagemNotFoundException("mensagem não encontrada"));


            mockMvc.perform(get("/mensagens/{id}", id)
                            .contentType(MediaType.APPLICATION_JSON))
//                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("requição apresenta erro"))
                    .andExpect(jsonPath("$.errors.[0]").value("mensagem não encontrada"));
            assertThat(output).contains("requisição para buscar mensagem foi efetuada");
            verify(mensagemService, times(1)).buscarMensagem(any(UUID.class));
        }
    }

    @Nested
    class AlterarMensagem {

        @Test
        void devePermirirAlterarMensagem(CapturedOutput output) throws Exception {
            var id = UUID.randomUUID();
            var mensagemAntiga = MensagemHelper.gerarMensagem();
            mensagemAntiga.setId(id);
            var mensagemNova = mensagemAntiga.toBuilder().build();
            mensagemNova.setConteudo("nova mensagem");

            when(mensagemService.buscarMensagem(any(UUID.class)))
                    .thenReturn(mensagemAntiga);
            when(mensagemService.alterarMensagem(any(Mensagem.class), any(Mensagem.class)))
                    .thenReturn(mensagemNova);

            mockMvc.perform(put("/mensagens/{id}", id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(mensagemNova)))
//                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(mensagemNova.getId().toString()))
                    .andExpect(jsonPath("$.conteudo").value(mensagemNova.getConteudo()))
                    .andExpect(jsonPath("$.usuario").value(mensagemNova.getUsuario()))
                    .andExpect(jsonPath("$.dataCriacao").value(mensagemNova.getDataCriacao()))
                    .andExpect(jsonPath("$.gostei").value(mensagemNova.getGostei()));
            assertThat(output).contains("requisição para atualizar mensagem foi efetuada");
            verify(mensagemService, times(1))
                    .buscarMensagem(any(UUID.class));
            verify(mensagemService, times(1))
                    .alterarMensagem(any(Mensagem.class), any(Mensagem.class));
        }

        @Test
        void deveGerarExcecao_QuandoAlterar_IdNaoCoincide(CapturedOutput output) throws Exception {
            var id = UUID.randomUUID();
            var mensagemRequest = MensagemHelper.gerarMensagem();
            mensagemRequest.setId(id);

            when(mensagemService.buscarMensagem(any(UUID.class)))
                    .thenThrow(new MensagemNotFoundException("mensagem não encontrada"));

            mockMvc.perform(put("/mensagens/{id}", id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(mensagemRequest)))
//                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("requição apresenta erro"))
                    .andExpect(jsonPath("$.errors.[0]").value("mensagem não encontrada"));
            assertThat(output).contains("requisição para atualizar mensagem foi efetuada");
            verify(mensagemService, never()).apagarMensagem(any(UUID.class));
        }

    }

    @Nested
    class RemoverMensagem {

        @Test
        void devePermitirApagarMensagem(CapturedOutput output) throws Exception {
            var id = UUID.randomUUID();
            doNothing().when(mensagemService).apagarMensagem(any(UUID.class));

            mockMvc.perform(delete("/mensagens/{id}", id))
//                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().string("mensagem removida"));
            assertThat(output).contains("requisição para apagar mensagem foi efetuada");
            verify(mensagemService, times(1)).apagarMensagem(any(UUID.class));
        }

        @Test
        void deveGerarExcecao_QuandoApagarMensagem_IdNaoExistente(CapturedOutput output) throws Exception {
            var id = UUID.randomUUID();

            when(mensagemService.buscarMensagem(any(UUID.class)))
                    .thenThrow(new MensagemNotFoundException("mensagem não encontrada"));

            mockMvc.perform(delete("/mensagens/{id}", id)
                            .contentType(MediaType.APPLICATION_JSON))
//                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("requição apresenta erro"))
                    .andExpect(jsonPath("$.errors.[0]").value("mensagem não encontrada"));
            assertThat(output).contains("requisição para apagar mensagem foi efetuada");
            verify(mensagemService, never()).apagarMensagem(any(UUID.class));
        }

    }

    @Nested
    class IncrementarGostei {

        @Test
        void devePermitirIncrementarGostei(CapturedOutput output) throws Exception {
            var id = UUID.randomUUID();
            var mensagemOriginal = MensagemHelper.gerarMensagem();
            mensagemOriginal.setId(id);
            mensagemOriginal.setDataCriacao(LocalDateTime.now());
            var mensagemModificada = mensagemOriginal.toBuilder().build();
            mensagemModificada.setGostei(mensagemModificada.getGostei() + 1);

            when(mensagemService.buscarMensagem(any(UUID.class))).thenReturn(mensagemOriginal);
            when(mensagemService.incrementarGostei(any(Mensagem.class))).thenReturn(mensagemModificada);

            mockMvc.perform(put("/mensagens/{id}/gostei", id)
                            .contentType(MediaType.APPLICATION_JSON))
//                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(mensagemOriginal.getId().toString()))
                    .andExpect(jsonPath("$.conteudo").value(mensagemOriginal.getConteudo()))
                    .andExpect(jsonPath("$.usuario").value(mensagemOriginal.getUsuario()))
                    .andExpect(jsonPath("$.dataCriacao").exists())
                    .andExpect(jsonPath("$.gostei").exists())
                    .andExpect(jsonPath("$.gostei").value(1));
            assertThat(output).contains("requisição para incrementar gostei foi efetuada");
            verify(mensagemService, times(1)).buscarMensagem(any(UUID.class));
        }

        @Test
        void deveGerarExcecao_QuandoIncrementarGostei_IdNaoExistente(CapturedOutput output) throws Exception {
            var id = UUID.randomUUID();

            when(mensagemService.buscarMensagem(any(UUID.class)))
                    .thenThrow(new MensagemNotFoundException("mensagem não encontrada"));

            mockMvc.perform(put("/mensagens/{id}/gostei", id)
                            .contentType(MediaType.APPLICATION_JSON))
//                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("requição apresenta erro"))
                    .andExpect(jsonPath("$.errors.[0]").value("mensagem não encontrada"));
            assertThat(output).contains("requisição para incrementar gostei foi efetuada");
            verify(mensagemService, never()).apagarMensagem(any(UUID.class));
        }
    }

    @Nested
    class ListarMensagem {

        @Test
        void devePermitirListarMensagens(CapturedOutput output) throws Exception {
            var mensagem = MensagemHelper.gerarMensagemCompleta();
            Page<Mensagem> page = new PageImpl<>(Arrays.asList(
                    mensagem
            ));
            when(mensagemService.listarMensagens(any(Pageable.class)))
                    .thenReturn(page);
            mockMvc.perform(get("/mensagens")
                            .contentType(MediaType.APPLICATION_JSON))
//                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].id").value(mensagem.getId().toString()))
                    .andExpect(jsonPath("$.content[0].conteudo").value(mensagem.getConteudo()))
                    .andExpect(jsonPath("$.content[0].usuario").value(mensagem.getUsuario()))
                    .andExpect(jsonPath("$.content[0].dataCriacao").exists())
                    .andExpect(jsonPath("$.content[0].gostei").exists());
            assertThat(output).contains("requisição para listar mensagens foi efetuada: Página=0, Tamanho=10");
            verify(mensagemService, times(1)).listarMensagens(any(Pageable.class));
        }

        @Test
        void devePermitirListarMensagens_QuandoNaoExisteRegistro(CapturedOutput output) throws Exception {
            Page<Mensagem> page = new PageImpl<>(Collections.emptyList());
            when(mensagemService.listarMensagens(any(Pageable.class)))
                    .thenReturn(page);
            mockMvc.perform(get("/mensagens")
                            .contentType(MediaType.APPLICATION_JSON))
//                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content", empty()))
                    .andExpect(jsonPath("$.content", hasSize(0)));
            assertThat(output).contains("requisição para listar mensagens foi efetuada: Página=0, Tamanho=10");
            verify(mensagemService, times(1)).listarMensagens(any(Pageable.class));
        }

    }


    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.MensagemRequest;
import org.example.handler.GlobalExceptionHandler;
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
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(OutputCaptureExtension.class)
@DisplayNameGeneration(DisplayTestName.class)
class MensagemControllerTest {

    private MockMvc mockMvc;

    @Mock
    private MensagemService mensagemService;

//    private GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();

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
                    .andDo(print())
                    .andExpect(status().isCreated());
            verify(mensagemService, times(1)).criarMensagem(any(Mensagem.class));
            assertThat(output).contains("requisição para registrar mensagem foi efetuada");
        }

        @Test
        void deveGerarExcecao_QuandoRegistrarMensagem_UsuarioEmBranco(CapturedOutput output) throws Exception {
            var mensagemRequest = MensagemRequest.builder()
                    .usuario("")
                    .conteudo("xpto")
                    .build();

            when(mensagemService.criarMensagem(any(Mensagem.class))).thenAnswer(i -> i.getArgument(0));

            mockMvc.perform(post("/mensagens")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(mensagemRequest)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Validation error"))
                    .andExpect(jsonPath("$.errors.[0]").value("usuário não pode estar vazio"));

            verify(mensagemService, never()).criarMensagem(any(Mensagem.class));
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

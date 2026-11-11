package dev.java10x.MagicFridgeAI;

import dev.java10x.MagicFridgeAI.enums.Categoria;
import dev.java10x.MagicFridgeAI.exception.AlimentoInvalidoException;
import dev.java10x.MagicFridgeAI.service.ValidadorDeAlimento;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class FoodItemApiTest {

    @Autowired
    MockMvc mvc;

    @MockitoBean
    ValidadorDeAlimento validador;

    @Test
    void cadastraItemValido() throws Exception {
        when(validador.validar("banana"))
                .thenReturn(new ValidadorDeAlimento.Resultado("Banana", Categoria.FRUTAS));

        mvc.perform(post("/food-items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nome":"banana","quantidade":3,"unidade":"UNIDADE","validade":"2026-12-01"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Banana"))
                .andExpect(jsonPath("$.categoria").value("FRUTAS"));
    }

    @Test
    void recusaCamposInvalidos() throws Exception {
        mvc.perform(post("/food-items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nome":"","quantidade":0}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Dados inválidos"));
    }

    @Test
    void recusaQuemNaoEhAlimento() throws Exception {
        when(validador.validar("Gustavo"))
                .thenThrow(new AlimentoInvalidoException("“Gustavo” não parece ser um alimento."));

        mvc.perform(post("/food-items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nome":"Gustavo","quantidade":1,"unidade":"UNIDADE","validade":"2026-12-01"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Alimento inválido"));
    }

    @Test
    void listaItens() throws Exception {
        mvc.perform(get("/food-items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}
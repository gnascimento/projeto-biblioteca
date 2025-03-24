package br.com.caffeineti.biblioteca.integration;

import br.com.caffeineti.biblioteca.model.Autor;
import br.com.caffeineti.biblioteca.repository.AutorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AutorRepositoryIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoSpyBean
    private AutorRepository autorRepository;

    @LocalServerPort
    private int port;

    @BeforeEach
    void setup() {
        autorRepository.deleteAll();

        Autor autor1 = new Autor();
        autor1.setNome("Joshua Bloch");
        autorRepository.save(autor1);

        Autor autor2 = new Autor();
        autor2.setNome("Martin Fowler");
        autorRepository.save(autor2);
    }

    @Test
    void deveListarAutoresComOrdenacaoPadraoPorId() throws Exception {
        mockMvc.perform(get("/autores")
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.autores", hasSize(2)))
                .andExpect(jsonPath("$._embedded.autores[0].codau", notNullValue()))
                .andExpect(jsonPath("$._embedded.autores[0].nome", is("Joshua Bloch")))
                .andExpect(jsonPath("$._embedded.autores[1].codau", notNullValue()))
                .andExpect(jsonPath("$._embedded.autores[1].nome", is("Martin Fowler")));
    }

    @Test
    void deveCriarNovoAutor() throws Exception {
        String novoAutorJson = """
        {
          "nome": "Robert C. Martin"
        }
        """;

        mockMvc.perform(post("/autores")
                        .contentType("application/json")
                        .content(novoAutorJson))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));

        var lista = autorRepository.findAll();
        assertEquals(3, lista.size());
    }

    @Test
    void deveFalharCriarNovoAutorComNomeVazio() throws Exception {
        String novoAutorJson = """
        {
          "nome": ""
        }
        """;

        mockMvc.perform(post("/autores")
                        .contentType("application/json")
                        .content(novoAutorJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].property", is("nome")))
                .andExpect(jsonPath("$.errors[0].invalidValue", is("")));

        var lista = autorRepository.findAll();
        assertEquals(2, lista.size());
    }

    @Test
    void deveBuscarPorId() throws Exception {
        Long idExistente = autorRepository.findAll().get(0).getCodau();

        mockMvc.perform(get("/autores/{id}", idExistente)
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome", anyOf(is("Joshua Bloch"), is("Martin Fowler"))));
    }

    @Test
    void deveAtualizarAutor() throws Exception {
        Autor autorExistente = autorRepository.findAll().get(0);
        Long idExistente = autorExistente.getCodau();

        String autorAtualizadoJson = String.format("""
        {
          "nome": "Novo nome para Autor %d"
        }
        """, idExistente);

        mockMvc.perform(put("/autores/{id}", idExistente)
                        .contentType("application/json")
                        .content(autorAtualizadoJson))
                .andExpect(status().isNoContent());

        Autor atualizado = autorRepository.findById(idExistente).orElseThrow();
        assertEquals(
                "Novo nome para Autor " + idExistente,
                atualizado.getNome()
        );
    }

    @Test
    void deveDeletarAutor() throws Exception {
        Long idExistente = autorRepository.findAll().get(0).getCodau();

        mockMvc.perform(delete("/autores/{id}", idExistente))
                .andExpect(status().isNoContent());

        assertFalse(autorRepository.existsById(idExistente));
    }

    @Test
    void deveLancarExcecaoQuandoFindAllFalhar() throws Exception {
        var pageable = PageRequest.of(0, 20);
        doThrow(new RuntimeException("Erro ao acessar o banco de dados")).when(autorRepository).findAll(pageable);

        mockMvc.perform(get("/autores")
                        .param("page", "0")
                        .param("size", "20")
                        .contentType("application/json"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message", is("Um erro desconhecido ocorreu.")))
                .andExpect(jsonPath("$.details", is("Erro ao acessar o banco de dados")));
    }
}
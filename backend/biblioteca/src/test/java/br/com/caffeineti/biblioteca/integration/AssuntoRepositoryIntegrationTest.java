package br.com.caffeineti.biblioteca.integration;

import br.com.caffeineti.biblioteca.model.Assunto;
import br.com.caffeineti.biblioteca.repository.AssuntoRepository;
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

/**
 * Teste de integração que sobe todo o contexto do SpringBoot,
 * utiliza uma base de dados de teste (em memória) e verifica os endpoints expostos.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AssuntoRepositoryIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoSpyBean
    private AssuntoRepository assuntoRepository;

    @LocalServerPort
    private int port;

    /**
     * Limpa a base e insere dados antes de cada teste.
     * Você pode substituir por @Sql ou simplesmente usar a API do repositório
     * para criar registros iniciais.
     */
    @BeforeEach
    void setup() {
        assuntoRepository.deleteAll();

        Assunto assunto1 = new Assunto();
        assunto1.setDescricao("Programação");
        assuntoRepository.save(assunto1);

        Assunto assunto2 = new Assunto();
        assunto2.setDescricao("Banco de Dados");
        assuntoRepository.save(assunto2);
    }

    @Test
    void deveListarAssuntosComOrdenacaoPadraoPorId() throws Exception {
        /**
         * Se nenhum parâmetro "sort" for passado, o repositório
         * deve retornar ordenado por id ascending, conforme a lógica do método findAll.
         */
        mockMvc.perform(get("/assuntos")
                        .contentType("application/json"))
                .andExpect(status().isOk())
                // Verifica que há 2 elementos no _embedded.assuntos
                .andExpect(jsonPath("$._embedded.assuntos", hasSize(2)))
                // Verifica se o primeiro é o ID 1 (ordem por ID)
                .andExpect(jsonPath("$._embedded.assuntos[0].codas", notNullValue()))
                .andExpect(jsonPath("$._embedded.assuntos[0].descricao", is("Programação")))
                .andExpect(jsonPath("$._embedded.assuntos[1].codas", notNullValue()))
                .andExpect(jsonPath("$._embedded.assuntos[1].descricao", is("Banco de Dados")));
    }

    @Test
    void deveCriarNovoAssunto() throws Exception {
        // O JSON para criar um novo registro
        String novoAssuntoJson = """
        {
          "descricao": "Arquitetura de Software"
        }
        """;

        mockMvc.perform(post("/assuntos")
                        .contentType("application/json")
                        .content(novoAssuntoJson))
                .andExpect(status().isCreated())
                // Verifica se o retorno do Location ou do corpo tem as infos adequadas
                .andExpect(header().exists("Location"));

        // Verifica se foi inserido no banco
        var lista = assuntoRepository.findAll();
        // Devem existir 3 assuntos agora
        assertEquals(3, lista.size());
    }

    @Test
    void deveFalharCriarNovoAssuntoComDescricaoVazia() throws Exception {
        // O JSON para criar um novo registro
        String novoAssuntoJson = """
        {
          "descricao": ""
        }
        """;

        mockMvc.perform(post("/assuntos")
                        .contentType("application/json")
                        .content(novoAssuntoJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].property", is("descricao")))
                .andExpect(jsonPath("$.errors[0].invalidValue", is("")));

        // Verifica se foi inserido no banco
        var lista = assuntoRepository.findAll();
        // Devem existir 2 assuntos agora
       assertEquals(2, lista.size());
    }

    @Test
    void deveBuscarPorId() throws Exception {
        // Pegando qualquer id existente (por exemplo, o menor) para efetuar GET
        Long idExistente = assuntoRepository.findAll().getFirst().getCodas();

        mockMvc.perform(get("/assuntos/{id}", idExistente)
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.descricao", anyOf(is("Programação"), is("Banco de Dados"))));
    }

    @Test
    void deveAtualizarAssunto() throws Exception {
        Assunto assuntoExistente = assuntoRepository.findAll().getFirst();
        Long idExistente = assuntoExistente.getCodas();

        // JSON para atualizar o campo "nome"
        String assuntoAtualizadoJson = String.format("""
        {
          "descricao": "Nova descrição para Assunto %d"
        }
        """, idExistente);

        mockMvc.perform(put("/assuntos/{id}", idExistente)
                        .contentType("application/json")
                        .content(assuntoAtualizadoJson))
                .andExpect(status().isNoContent());

        // Verifica se atualizou no banco
        Assunto atualizado = assuntoRepository.findById(idExistente).orElseThrow();
        assertEquals(
                "Nova descrição para Assunto " + idExistente,
                atualizado.getDescricao()
        );
    }

    @Test
    void deveDeletarAssunto() throws Exception {
        // Pega algum ID existente e deleta
        Long idExistente = assuntoRepository.findAll().getFirst().getCodas();

        mockMvc.perform(delete("/assuntos/{id}", idExistente))
                .andExpect(status().isNoContent());

        assertFalse(assuntoRepository.existsById(idExistente));
    }

    @Test
    void deveLancarExcecaoQuandoFindAllFalhar() throws Exception {
        var pageable = PageRequest.of(
                0,
                20
        );
        // Configura o mock para lançar uma exceção quando findAll for chamado
        doThrow(new RuntimeException("Erro ao acessar o banco de dados")).when(assuntoRepository).findAll(pageable);

        mockMvc.perform(get("/assuntos")
                        .param("page", "0")
                        .param("size", "20")
                        .contentType("application/json"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message", is("Um erro desconhecido ocorreu.")))
                .andExpect(jsonPath("$.details", is("Erro ao acessar o banco de dados")));
    }

}


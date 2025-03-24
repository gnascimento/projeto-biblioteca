package br.com.caffeineti.biblioteca.integration;

import br.com.caffeineti.biblioteca.model.Assunto;
import br.com.caffeineti.biblioteca.model.Autor;
import br.com.caffeineti.biblioteca.model.Livro;
import br.com.caffeineti.biblioteca.repository.AssuntoRepository;
import br.com.caffeineti.biblioteca.repository.AutorRepository;
import br.com.caffeineti.biblioteca.repository.LivroRepository;
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

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class LivroRepositoryIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoSpyBean
    private LivroRepository livroRepository;

    @Autowired
    private AssuntoRepository assuntoRepository;

    @LocalServerPort
    private int port;
    @Autowired
    private AutorRepository autorRepository;

    private List<Autor> autores;

    private List<Assunto> assuntos;

    @BeforeEach
    void setup() {

        livroRepository.deleteAll();
        assuntoRepository.deleteAll();
        autorRepository.deleteAll();

        Assunto assunto1 = new Assunto();
        assunto1.setDescricao("Programação");
        assuntoRepository.save(assunto1);

        Autor autor1 = new Autor();
        autor1.setNome("Joshua Bloch");

        Autor autor2 = new Autor();
        autor2.setNome("Robert C. Martin");

        autorRepository.save(autor1);
        autorRepository.save(autor2);

        Livro livro1 = new Livro();
        livro1.setTitulo("Effective Java");
        livro1.setEditora("Addison-Wesley");
        livro1.setEdicao(3);
        livro1.setAnoPublicacao("2018");
        livro1.setAutores(List.of(autor1));
        livro1.setAssuntos(List.of(assunto1));
        livroRepository.save(livro1);

        Livro livro2 = new Livro();
        livro2.setTitulo("Clean Code");
        livro2.setEditora("Prentice Hall");
        livro2.setEdicao(1);
        livro2.setAnoPublicacao("2008");
        livro2.setAutores(List.of(autor2));
        livro2.setAssuntos(List.of(assunto1));
        livroRepository.save(livro2);

        assuntos = assuntoRepository.findAll();
        autores = autorRepository.findAll();
    }

    @Test
    void deveListarLivrosComOrdenacaoPadraoPorId() throws Exception {
        mockMvc.perform(get("/livros")
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.livros", hasSize(2)))
                .andExpect(jsonPath("$._embedded.livros[0].codl", notNullValue()))
                .andExpect(jsonPath("$._embedded.livros[0].titulo", is("Effective Java")))
                .andExpect(jsonPath("$._embedded.livros[1].codl", notNullValue()))
                .andExpect(jsonPath("$._embedded.livros[1].titulo", is("Clean Code")));
    }

    @Test
    void deveCriarNovoLivro() throws Exception {
        String novoLivroJson = String.format("""
                        {
                          "titulo": "Refactoring",
                          "editora": "Addison-Wesley",
                          "edicao": 2,
                          "anoPublicacao": "2018",
                          "autores": ["%s"],
                          "assuntos": ["%s"]
                        }
                        """,
                "/autores/" + autores.getFirst().getCodau(),
                "/assuntos/" + assuntos.getFirst().getCodas());

        mockMvc.perform(post("/livros")
                        .contentType("application/json")
                        .content(novoLivroJson))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));

        var lista = livroRepository.findAll();
        assertEquals(3, lista.size());
    }

    @Test
    void deveFalharCriarNovoLivroComTituloVazio() throws Exception {
        String novoLivroJson = String.format("""
                        {
                          "titulo": "",
                          "editora": "Addison-Wesley",
                          "edicao": 2,
                          "anoPublicacao": "2018",
                          "autores": ["%s"],
                          "assuntos": ["%s"]
                        }
                        """,
                "/autores/" + autores.getFirst().getCodau(),
                "/assuntos/" + assuntos.getFirst().getCodas());

        mockMvc.perform(post("/livros")
                        .contentType("application/json")
                        .content(novoLivroJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].property", is("titulo")))
                .andExpect(jsonPath("$.errors[0].invalidValue", is("")));

        var lista = livroRepository.findAll();
        assertEquals(2, lista.size());
    }

    @Test
    void deveBuscarPorId() throws Exception {
        Long idExistente = livroRepository.findAll().get(0).getCodl();

        mockMvc.perform(get("/livros/{id}", idExistente)
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo", anyOf(is("Effective Java"), is("Clean Code"))));
    }

    @Test
    void deveAtualizarLivro() throws Exception {
        Livro livroExistente = livroRepository.findAll().get(0);
        Long idExistente = livroExistente.getCodl();

        String livroAtualizadoJson = String.format("""
                {
                  "titulo": "Novo título para Livro %d",
                  "editora": "Nova Editora",
                  "edicao": 4,
                  "anoPublicacao": "2022",
                  "autores": ["%s"],
                  "assuntos": ["%s"]
                }
                """, idExistente,
                "/autores/" + autores.getFirst().getCodau(),
                "/assuntos/" + assuntos.getFirst().getCodas());

        mockMvc.perform(put("/livros/{id}", idExistente)
                        .contentType("application/json")
                        .content(livroAtualizadoJson))
                .andExpect(status().isNoContent());

        Livro atualizado = livroRepository.findById(idExistente).orElseThrow();
        assertEquals(
                "Novo título para Livro " + idExistente,
                atualizado.getTitulo()
        );
    }

    @Test
    void deveDeletarLivro() throws Exception {
        Long idExistente = livroRepository.findAll().get(0).getCodl();

        mockMvc.perform(delete("/livros/{id}", idExistente))
                .andExpect(status().isNoContent());

        assertFalse(livroRepository.existsById(idExistente));
    }

    @Test
    void deveLancarExcecaoQuandoFindAllFalhar() throws Exception {
        var pageable = PageRequest.of(0, 20);
        doThrow(new RuntimeException("Erro ao acessar o banco de dados")).when(livroRepository).findAll(pageable);

        mockMvc.perform(get("/livros")
                        .param("page", "0")
                        .param("size", "20")
                        .contentType("application/json"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message", is("Um erro desconhecido ocorreu.")))
                .andExpect(jsonPath("$.details", is("Erro ao acessar o banco de dados")));
    }
}

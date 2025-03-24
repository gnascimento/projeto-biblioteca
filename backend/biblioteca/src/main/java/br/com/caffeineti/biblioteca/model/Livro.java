package br.com.caffeineti.biblioteca.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Schema(description = "Representa um livro na biblioteca")
@Data
public class Livro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único do livro", example = "1")
    private Long codl;

    @NotBlank
    @Schema(description = "Título do livro", example = "Effective Java")
    @Column(nullable = false)
    private String titulo;

    @NotBlank
    @Schema(description = "Editora do livro", example = "Addison-Wesley")
    @Column(nullable = false)
    private String editora;

    @Positive
    @Schema(description = "Edição do livro", example = "3")
    private Integer edicao;

    @Column(name = "ano_publicacao")
    @Positive
    @Schema(description = "Ano de publicação", example = "2018")
    private String anoPublicacao;

    private BigDecimal valor;

    @ManyToMany(cascade = CascadeType.MERGE)
    @JoinTable(
            name = "livro_autor",
            joinColumns = @JoinColumn(name = "livro_codl"),
            inverseJoinColumns = @JoinColumn(name = "autor_codau")
    )
    @Schema(description = "Lista de autores do livro", type = "array", implementation = Autor.class)
    @NotEmpty
    private List<Autor> autores;

    @ManyToMany(cascade = CascadeType.MERGE)
    @JoinTable(
            name = "livro_assunto",
            joinColumns = @JoinColumn(name = "livro_codl"),
            inverseJoinColumns = @JoinColumn(name = "assunto_codas")
    )
    @Schema(description = "Lista de assuntos do livro", type = "array", implementation = Assunto.class)
    @JsonProperty
    @NotEmpty
    private List<Assunto> assuntos;

}
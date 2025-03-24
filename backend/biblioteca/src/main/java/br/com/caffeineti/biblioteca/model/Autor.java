package br.com.caffeineti.biblioteca.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Entity
@Schema(description = "Representa um autor na biblioteca")
@Data
public class Autor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único do autor", example = "1")
    private Long codau;

    @NotBlank
    @Schema(description = "Nome do autor", example = "Joshua Bloch")
    @Column(nullable = false)
    private String nome;

    @ManyToMany(mappedBy = "autores")
    @Schema(description = "Lista de livros do autor")
    @JsonIgnore
    private List<Livro> livros;
}
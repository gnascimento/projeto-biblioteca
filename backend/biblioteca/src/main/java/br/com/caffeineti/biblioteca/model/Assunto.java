package br.com.caffeineti.biblioteca.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Entity
@Schema(description = "Representa um assunto na biblioteca")
@Data
public class Assunto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long codas;

    @NotEmpty
    @Schema(description = "Descrição do assunto", example = "Programação")
    @NotBlank
    @Column(nullable = false)
    private String descricao;

    @ManyToMany(mappedBy = "assuntos")
    @Schema(description = "Lista de livros relacionados ao assunto")
    @JsonIgnore
    private List<Livro> livros;

}
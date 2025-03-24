package br.com.caffeineti.biblioteca.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "view_relatorio")
@Immutable
public class ViewRelatorio {
    @Id
    private String compositeId;
    private String autor;
    private String assunto;
    private Long codl;
    private String titulo;
    private Double valor;
    private Integer anoPublicacao;
    private String editora;
    private String edicao;
}
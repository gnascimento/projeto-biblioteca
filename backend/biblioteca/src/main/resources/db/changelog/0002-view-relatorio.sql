--liquibase formatted sql
--changeset view-relatorio:1
CREATE OR REPLACE VIEW view_relatorio AS
SELECT
    a.nome AS autor,
    asu.descricao AS assunto,
    l.codl,
    l.titulo,
    l.valor,
    l.ano_publicacao,
    l.editora,
    l.edicao,
    CONCAT(l.codl, '-', asu.codas, '-', a.codau) AS composite_id
FROM autor a
         JOIN livro_autor la ON a.codau = la.autor_codau
         JOIN livro l ON l.codl = la.livro_codl
         JOIN livro_assunto ls ON l.codl = ls.livro_codl
         JOIN assunto asu ON ls.assunto_codas = asu.codas
ORDER BY a.nome, asu.descricao, l.titulo;

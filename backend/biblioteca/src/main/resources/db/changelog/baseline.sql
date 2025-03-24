--liquibase formatted sql
--changeset tabela-livro:1
CREATE TABLE IF NOT EXISTS livro
(
    codl          BIGSERIAL PRIMARY KEY,
    titulo        VARCHAR(100) NOT NULL,
    editora       VARCHAR(100),
    edicao        INTEGER,
    ano_publicacao VARCHAR(4)
);

--changeset tabela-autor:2
CREATE TABLE IF NOT EXISTS autor
(
    codau BIGSERIAL PRIMARY KEY,
    nome  VARCHAR(100) NOT NULL
);

--changeset tabela-assunto:3
CREATE TABLE IF NOT EXISTS assunto
(
    codas     BIGSERIAL PRIMARY KEY,
    descricao VARCHAR(100) NOT NULL
);

--changeset tabela-livro-autor:4
CREATE TABLE IF NOT EXISTS livro_autor
(
    livro_codl  BIGINT NOT NULL,
    autor_codau BIGINT NOT NULL,
    CONSTRAINT pk_livro_autor PRIMARY KEY (livro_codl, autor_codau),
    CONSTRAINT fk_livro_autor_livro FOREIGN KEY (livro_codl)
        REFERENCES livro (codl),
    CONSTRAINT fk_livro_autor_autor FOREIGN KEY (autor_codau)
        REFERENCES autor (codau)
);

--changeset tabela-livro-assunto:5
CREATE TABLE IF NOT EXISTS livro_assunto
(
    livro_codl    BIGINT NOT NULL,
    assunto_codas BIGINT NOT NULL,
    CONSTRAINT pk_livro_assunto PRIMARY KEY (livro_codl, assunto_codas),
    CONSTRAINT fk_livro_assunto_livro FOREIGN KEY (livro_codl)
        REFERENCES livro (codl),
    CONSTRAINT fk_livro_assunto_assunto FOREIGN KEY (assunto_codas)
        REFERENCES assunto (codas)
);


--changeset dados-iniciais:6
INSERT INTO autor (nome)
VALUES ('J.K. Rowling'),
       ('George R. R. Martin'),
       ('J.R.R. Tolkien'),
       ('Paulo Coelho'),
       ('Machado de Assis');

--changeset dados-iniciais:7
INSERT INTO assunto (descricao)
VALUES ('Fantasia'),
       ('Romance'),
       ('Ficção'),
       ('Autoajuda'),
       ('Clássico Brasileiro');

--changeset dados-iniciais:8
INSERT INTO livro (titulo, editora, edicao, ano_publicacao)
VALUES ('Harry Potter e a Pedra Filosofal', 'Rocco', 1, '1997'),
       ('A Guerra dos Tronos', 'Leya', 1, '1996'),
       ('O Senhor dos Anéis', 'Martins Fontes', 1, '1954'),
       ('O Alquimista', 'HarperCollins', 1, '1988'),
       ('Dom Casmurro', 'Editora Globo', 1, '1899');

--changeset dados-iniciais:9
INSERT INTO livro_autor (livro_codl, autor_codau)
VALUES ((SELECT codl FROM livro WHERE titulo = 'Harry Potter e a Pedra Filosofal'),
        (SELECT codau FROM autor WHERE nome = 'J.K. Rowling'));

--changeset dados-iniciais:10
INSERT INTO livro_autor (livro_codl, autor_codau)
VALUES ((SELECT codl FROM livro WHERE titulo = 'A Guerra dos Tronos'),
        (SELECT codau FROM autor WHERE nome = 'George R. R. Martin'));

--changeset dados-iniciais:11
INSERT INTO livro_autor (livro_codl, autor_codau)
VALUES ((SELECT codl FROM livro WHERE titulo = 'O Senhor dos Anéis'),
        (SELECT codau FROM autor WHERE nome = 'J.R.R. Tolkien'));

--changeset dados-iniciais:12
INSERT INTO livro_autor (livro_codl, autor_codau)
VALUES ((SELECT codl FROM livro WHERE titulo = 'O Alquimista'),
        (SELECT codau FROM autor WHERE nome = 'Paulo Coelho'));

--changeset dados-iniciais:13
INSERT INTO livro_autor (livro_codl, autor_codau)
VALUES ((SELECT codl FROM livro WHERE titulo = 'Dom Casmurro'),
        (SELECT codau FROM autor WHERE nome = 'Machado de Assis'));

--changeset dados-iniciais:14
INSERT INTO livro_assunto (livro_codl, assunto_codas)
VALUES ((SELECT codl FROM livro WHERE titulo = 'Harry Potter e a Pedra Filosofal'),
        (SELECT codas FROM assunto WHERE descricao = 'Fantasia'));

--changeset dados-iniciais:15
INSERT INTO livro_assunto (livro_codl, assunto_codas)
VALUES ((SELECT codl FROM livro WHERE titulo = 'Harry Potter e a Pedra Filosofal'),
        (SELECT codas FROM assunto WHERE descricao = 'Ficção'));

--changeset dados-iniciais:16
INSERT INTO livro_assunto (livro_codl, assunto_codas)
VALUES ((SELECT codl FROM livro WHERE titulo = 'A Guerra dos Tronos'),
        (SELECT codas FROM assunto WHERE descricao = 'Fantasia'));

--changeset dados-iniciais:17
INSERT INTO livro_assunto (livro_codl, assunto_codas)
VALUES ((SELECT codl FROM livro WHERE titulo = 'A Guerra dos Tronos'),
        (SELECT codas FROM assunto WHERE descricao = 'Ficção'));

--changeset dados-iniciais:18
INSERT INTO livro_assunto (livro_codl, assunto_codas)
VALUES ((SELECT codl FROM livro WHERE titulo = 'O Senhor dos Anéis'),
        (SELECT codas FROM assunto WHERE descricao = 'Fantasia'));

--changeset dados-iniciais:19
INSERT INTO livro_assunto (livro_codl, assunto_codas)
VALUES ((SELECT codl FROM livro WHERE titulo = 'O Alquimista'),
        (SELECT codas FROM assunto WHERE descricao = 'Romance'));

--changeset dados-iniciais:20
INSERT INTO livro_assunto (livro_codl, assunto_codas)
VALUES ((SELECT codl FROM livro WHERE titulo = 'O Alquimista'),
        (SELECT codas FROM assunto WHERE descricao = 'Autoajuda'));

--changeset dados-iniciais:21
INSERT INTO livro_assunto (livro_codl, assunto_codas)
VALUES ((SELECT codl FROM livro WHERE titulo = 'Dom Casmurro'),
        (SELECT codas FROM assunto WHERE descricao = 'Clássico Brasileiro'));

--changeset dados-iniciais:22
INSERT INTO livro_assunto (livro_codl, assunto_codas)
VALUES ((SELECT codl FROM livro WHERE titulo = 'Dom Casmurro'),
        (SELECT codas FROM assunto WHERE descricao = 'Romance'));

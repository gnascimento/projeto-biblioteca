--liquibase formatted sql
--changeset novo-campo-valor:1
ALTER TABLE livro
    ADD COLUMN valor DECIMAL(10, 2);

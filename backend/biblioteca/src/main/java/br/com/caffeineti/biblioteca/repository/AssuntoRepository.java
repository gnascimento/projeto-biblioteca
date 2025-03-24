package br.com.caffeineti.biblioteca.repository;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import br.com.caffeineti.biblioteca.model.Assunto;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.lang.NonNull;

@RepositoryRestResource(path = "assuntos",
        collectionResourceRel = "assuntos",
        itemResourceRel = "assunto")
@Tag(name = "Assunto", description = "API para acesso aos assuntos da biblioteca")
public interface AssuntoRepository extends JpaRepository<Assunto, Long> {


    @RestResource(path = "by-descricao", rel = "autores")
    @Query("""
            select a from Assunto a
            where (:descricao is null or lower(a.descricao) like lower(concat('%', cast(:descricao as string), '%')))
            order by a.codas
            """)
    Page<Assunto> findByDescricaoContainingIgnoreCase(@Param("descricao") String descricao, Pageable pageable);

    @RestResource(rel = "assuntos")  // substitui o endpoint principal
    @NonNull default Page<Assunto> findAll(@NonNull Pageable pageable) {
        if (!pageable.getSort().isSorted()) {
            // Caso o cliente não tenha especificado nenhum "sort", forçamos por "id".
            pageable = PageRequest.of(
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    Sort.by("codas").ascending()
            );
        }

        // Precisamos chamar outro método interno, para evitar recursão chamando "findAllWithDefaultSort" de novo
        return findAllInterno(pageable);
    }

    @Query("select a from Assunto a")
    @RestResource(exported = false)
    Page<Assunto> findAllInterno(Pageable pageable);
}
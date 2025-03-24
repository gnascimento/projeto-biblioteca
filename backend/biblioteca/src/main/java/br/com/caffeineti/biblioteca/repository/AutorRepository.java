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
import br.com.caffeineti.biblioteca.model.Autor;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.lang.NonNull;

@RepositoryRestResource(path = "autores",
        collectionResourceRel = "autores",
        itemResourceRel = "autor")
@Tag(name = "Autor", description = "API para acesso aos autores da biblioteca")
public interface AutorRepository extends JpaRepository<Autor, Long> {

    @RestResource(path = "by-nome", rel = "autores")
    @Query("""
            select a from Autor a
            where (:nome is null or lower(a.nome) like lower(concat('%', cast(:nome as string), '%')))
            order by a.codau
            """)
    Page<Autor> findByNomeContainingIgnoreCase(@Param("nome") String nome, Pageable pageable);

    @RestResource(rel = "autores")
    @NonNull default Page<Autor> findAll(@NonNull Pageable pageable) {
        if (!pageable.getSort().isSorted()) {
            pageable = PageRequest.of(
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    Sort.by("codau").ascending()
            );
        }
        return findAllInterno(pageable);
    }

    @Query("select a from Autor a")
    @RestResource(exported = false)
    Page<Autor> findAllInterno(Pageable pageable);
}
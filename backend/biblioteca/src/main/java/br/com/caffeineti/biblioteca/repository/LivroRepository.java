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
import br.com.caffeineti.biblioteca.model.Livro;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.lang.NonNull;


@RepositoryRestResource(path = "livros",
        collectionResourceRel = "livros",
        itemResourceRel = "livro")
@Tag(name = "Livro", description = "API para acesso aos livros da biblioteca")
public interface LivroRepository extends JpaRepository<Livro, Long> {

    @RestResource(path = "by-titulo", rel = "autores")
    @Query("""
            select l from Livro l
            where (:titulo is null or lower(l.titulo) like lower(concat('%', cast(:titulo as string), '%')))
            order by l.codl
            """)
    Page<Livro> findByNomeContainingIgnoreCase(@Param("titulo") String titulo, Pageable pageable);


    @RestResource(rel = "livros")
    @NonNull default Page<Livro> findAll(@NonNull Pageable pageable) {
        if (!pageable.getSort().isSorted()) {
            pageable = PageRequest.of(
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    Sort.by( "codl").ascending()
            );
        }
        return findAllInterno(pageable);
    }

    @Query("select l from Livro l")
    @RestResource(exported = false)
    Page<Livro> findAllInterno(Pageable pageable);
}
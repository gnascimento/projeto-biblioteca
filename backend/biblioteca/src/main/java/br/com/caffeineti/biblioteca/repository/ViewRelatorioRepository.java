package br.com.caffeineti.biblioteca.repository;

import br.com.caffeineti.biblioteca.model.Livro;
import br.com.caffeineti.biblioteca.model.ViewRelatorio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ViewRelatorioRepository extends JpaRepository<ViewRelatorio, Long> {


    @Query(value = """
        select v
        from ViewRelatorio v
        """)
    List<ViewRelatorio> findBooksComAutorAssunto();


}
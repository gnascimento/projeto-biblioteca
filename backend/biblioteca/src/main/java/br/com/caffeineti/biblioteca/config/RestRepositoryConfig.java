package br.com.caffeineti.biblioteca.config;

import br.com.caffeineti.biblioteca.model.Assunto;
import br.com.caffeineti.biblioteca.model.Autor;
import br.com.caffeineti.biblioteca.model.Livro;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.core.event.ValidatingRepositoryEventListener;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

@Configuration
public class RestRepositoryConfig implements RepositoryRestConfigurer {

    private final LocalValidatorFactoryBean validator;

    public RestRepositoryConfig(LocalValidatorFactoryBean validator) {
        this.validator = validator;
    }

    @Override
    public void configureValidatingRepositoryEventListener(
            ValidatingRepositoryEventListener validatingListener) {
        // “beforeCreate” e “beforeSave” são eventos padrão do Spring Data REST
        validatingListener.addValidator("beforeCreate", validator);
        validatingListener.addValidator("beforeSave", validator);
    }


    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config, CorsRegistry cors) {
        // Liste todas as entidades cujo ID você deseja expor
        config.exposeIdsFor(Assunto.class, Autor.class, Livro.class);

        // Configure CORS to allow all origins
        cors.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*");
    }
}

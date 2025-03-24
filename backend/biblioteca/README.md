# Biblioteca

## Descrição
Este é um projeto de exemplo de uma biblioteca, desenvolvido com Java, Spring Boot e Gradle. Ele utiliza JPA para persistência de dados e Liquibase para migrações de banco de dados.

## Estrutura do Projeto
- **Java**: Linguagem de programação principal.
- **Spring Boot**: Framework para criação de aplicações Java.
- **Gradle**: Ferramenta de automação de build.
- **JPA**: API de persistência de dados.
- **Liquibase**: Ferramenta de migração de banco de dados.

## Configuração do Projeto
### Pré-requisitos
- JDK 21 ou superior
- Gradle 8.5 ou superior
- PostgreSQL 17.4

### Configuração do Banco de Dados
Certifique-se de configurar o banco de dados no arquivo `application.properties`.

Para configurar o banco de dados no `application.properties` e iniciar um contêiner Docker com PostgreSQL para desenvolvimento local, siga os passos abaixo:

1. **Configuração do Banco de Dados no `application.properties`:**

   Certifique-se de que o arquivo `src/main/resources/application.properties` está configurado corretamente. Você pode usar as variáveis de ambiente, se desejar:
   ```ini
   # Configurações do Liquibase
   spring.liquibase.change-log=classpath:db/changelog/db.changelog-master.yaml
   spring.liquibase.enabled=true

   # Configurações do Banco de Dados
   spring.datasource.url=jdbc:postgresql://${DATABASE_HOST:localhost}:5432/biblioteca
   spring.datasource.username=${DATABASE_USERNAME:postgres}
   spring.datasource.password=${DATABASE_PASSWORD:admin}
   spring.datasource.driver-class-name=org.postgresql.Driver
   ```

2. **Iniciar um contêiner Docker com PostgreSQL:**

   Utilize o seguinte comando para iniciar um contêiner Docker com PostgreSQL e expor a porta 5432:

   ```sh
   docker run --name postgres-biblioteca  -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=admin -p 5432:5432 -d postgres:17.4
   ```
   Este comando cria e inicia um contêiner Docker com PostgreSQL, configurando o banco de dados `biblioteca` e expondo a porta 5432 para acesso local.
3. ** Criar o banco de dados `biblioteca` no PostgreSQL
    ```sh
    docker exec -it postgres-biblioteca psql -U postgres -c "CREATE DATABASE biblioteca"
    ```
### Executando a Aplicação
Para executar a aplicação, utilize o comando:
```sh
./gradlew bootRun
```
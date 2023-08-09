# Microsserviço de Administração do Catálogo de Vídeos - Parte II

O microsserviço de Administração do Catálogo de Vídeos é a aplicação _backend_ responsável por gerenciar os vídeos, incluindo as categorias, os gêneros e os membros do elenco.

Dentro da dinâmica do sistema:

1. A aplicação _Backend Admin_ do Catálogo de Vídeos vai falar com o banco de dados, salvar os dados dos vídeos, dos gêneros, das categorias e membros do elenco;
2. A aplicação _Frontend Admin_ do Catálogo de Vídeos vai falar com a _API_ do _backend_ para realizar as ações de cadastro;
3. A aplicação _Encoder_ de Vídeos vai acessar os vídeos que forem enviados via _Backend_ de Administração de Vídeos, fazer o _encoding_ e salvar os dados em um _bucket_ no _Google Cloud Storage_.

Esta segunda parte contempla o desenvolvimento para o Agregado (segundo _Domain-Driven Design_ (_DDD_)) de Gêneros.

Com relação ao _software design_, a aplicação segue uma arquitetura _middle-out_, baseada nos modelos de _Clean Architecture_ e _DDD_.

Estão envolvidas, na aplicação, tecnologias de:

- Backend

  - Java (JDK 17)
  - Spring Boot 3
  - Gradle (gerenciador de dependências)
  - Spring Data & JPA
  - MySQL
  - Flyway (gerenciamento do banco de dados)
  - H2 (testes integrados de persistência)
  - JUnit Jupiter (testes unitários)
  - Mockito JUnit Jupiter (testes integrados)
  - Testcontainers MySQL (testes end-to-end)
  - Springdoc-openapi (documentação da API)

O desenvolvimento da aplicação é baseado na metodologia _TDD_ (_Test-Driven Development_), sendo desenvolvidos:

- Testes unitários para a camada de _domain_ (ou _Entities_, segundo _Clean Architecture_) e de _application_ (ou _Use Cases_, segundo _Clean Architecture_);
- Testes de integração de persistência e _web_ para a camada de _infrastructure_ (ou _Frameworks_, segundo _Clean Architecture_);
- E, por fim, testes _end-to-end_ e de regressão manual via _Postman_.

### Relacionamentos entre Agregados

Uma dúvida comum em aplicações baseadas na abordagem de _Domain-Driven Design_ é como implementar os relacionamentos entre Agregados.

Existem, basicamente, duas abordagens: associar o objeto inteiro dentro de outra classe ou apenas o identificador.

Qual é a recomendação a seguir, tanto para a camada de Domínio quanto para a camada de Infraestrutura?

Recomenda-se seguir a abordagem de mapear apenas o identificador. Por quê?

Porque trata-se de dois Agregados diferentes e, conforme o conceito de Agregado, ele deveria ter a capacidade de ser independente dos demais, possuindo um repositório próprio de acesso à informação.

Além disso, evitam-se problemas de concorrência para o caso em que o objeto inteiro de um Agregado esteja associado a outro.

Por exemplo, ao atualizar o Agregado _A_, seria necessário atualizar também todo o objeto do Agregado _B_, porque eles estão associados e, se, neste mesmo momento, o Agregado _B_ estiver sendo atualizado por outro processo qualquer separado, como em uma tela de cadastro, a atualização do Agregado _A_ pode sobrescrever as atualizações do Agregado _B_.

Dessa forma, relacionando-se apenas pelo _ID_, não se altera mais o objeto inteiro do Agregado _B_ no momento em que se edita o Agregado _A_, é alterado apenas a referência para o seu _ID_.

Isso é bem detalhado no livro de _Vaughn Vernon_, _Implementing Domain-Driven Design_, no capítulo sobre _Aggregates_, que menciona que a estratégia de associar por _ID_ tem implicações positivas em questões de _performance_ também, porque, dessa forma, as instâncias carregam mais rapidamente, ocupando menos memória.

Um exemplo de implementação de relacionamento entre Agregados pode ser visto neste projeto entre os Agregados de Categorias e Gêneros.

#### Referências
FULL CYCLE 3.0. Projeto prático — Java ( Back-end ). 2023. Disponível em: https://plataforma.fullcycle.com.br. Acesso em: 09 ago. 2023.
VERNON, Vaughn. Implementing Domain-Driven Design. United States: Pearson Education, Inc., July 2013.

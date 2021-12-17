[![Build Status](https://app.travis-ci.com/MarcusViniciusCavalcanti/coopvotes.svg?branch=main)](https://app.travis-ci.com/MarcusViniciusCavalcanti/coopvotes)
[![Maintainability](https://api.codeclimate.com/v1/badges/5bb783c76acb254d0791/maintainability)](https://codeclimate.com/github/MarcusViniciusCavalcanti/coopvotes/maintainability)
[![codecov](https://codecov.io/gh/MarcusViniciusCavalcanti/coopvotes/branch/main/graph/badge.svg?token=IQGPQG17CT)](https://codecov.io/gh/MarcusViniciusCavalcanti/coopvotes)


## COOP-VOTES

Aplicação para prova de conceito, um exemplo de solução para uma aplicação de votação com alta
demanda.

As premissas:

A solução deve ser executada na nuvem e promover as seguintes funcionalidades através de uma API REST:

* Cadastrar uma nova pauta
* Abrir uma sessão de votação em uma pauta (a sessão de votação deve ficar aberta por um tempo determinado na chamada de abertura ou 1 minuto por default)
* Receber votos dos associados em pautas (os votos são apenas 'Sim'/'Não'. Cada associado é identificado por um id único e pode votar apenas uma vez por pauta)
* Contabilizar os votos e dar o resultado da votação na pauta
* Integração com sistemas externos
  * GET https://user-info.herokuapp.com/users/{cpf}
  * Caso o CPF seja inválido, a API retornará o HTTP Status 404 (Not found). Você pode usar geradores de CPF para gerar CPFs válidos
  * Caso o CPF seja válido, a API retornará se o usuário pode (ABLE_TO_VOTE) ou não pode (UNABLE_TO_VOTE) executar a operação
* O resultado da votação precisa ser informado para o restante da plataforma, isso deve ser feito preferencialmente através de mensageria. Quando a sessão de votação fechar, poste uma mensagem com o resultado da votação
* Aplicação possa ser usada em cenários que existam centenas de milhares de votos. Ela deve se comportar de maneira performática nesses cenários

A abordagem foi utilizar uma arquitetura orientada a eventos, utilizando [Rqueue](https://github.com/sonus21/rqueue)
para o gerenciamento dos eventos, Redis para notificações dos eventos, além de Kafka para notificação do resultado
final da Sessão de votação de uma Pauta.

### Documentação 
A documentação foi utilizado o [Redoc](https://github.com/Redocly/redoc) para gerar o arquivo Html para
ser disponibilizado o contrato e mais informações do comportamento da Api.
A documentação pode ser encontrada no caminho http://<ip_da_aplicação:porta_da_aplicação/

### Como Executar

A solução utiliza Java na versão 17, certifique de ter instalado. Assim como um banco de dados, recomentando 
Postgres, uma instância do Redis e também uma instância do Kafka configurado. 

__O arquivo docker-compose__ contém as configurações dos containers, de exemplo, para execução.

Com banco de dados, Redis e Kafka execute: ``mvn spring-boot:run``

### Controle de versão
Controle de versão adotado foi adoção no dominio da api, **Exemplo**: _https://ap.v1.coopvotes.online_
Toda resposta da API contém um Header com chave `Version-Api`  e o valor a versão da API exemplo: `1.0.0`.

### Dependências:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/2.5.6/maven-plugin/reference/html/)
* [Create an OCI image](https://docs.spring.io/spring-boot/docs/2.5.6/maven-plugin/reference/html/#build-image)
* [Testcontainers Kafka Modules Reference Guide](https://www.testcontainers.org/modules/kafka/)
* [Testcontainers Postgres Module Reference Guide](https://www.testcontainers.org/modules/databases/postgres/)
* [Spring Configuration Processor](https://docs.spring.io/spring-boot/docs/2.5.6/reference/htmlsingle/#configuration-metadata-annotation-processor)
* [Spring Data JPA](https://docs.spring.io/spring-boot/docs/2.5.6/reference/htmlsingle/#boot-features-jpa-and-spring-data)
* [Spring for Apache Kafka](https://docs.spring.io/spring-boot/docs/2.5.6/reference/htmlsingle/#boot-features-kafka)
* [Spring Boot DevTools](https://docs.spring.io/spring-boot/docs/2.5.6/reference/htmlsingle/#using-boot-devtools)
* [Liquibase Migration](https://docs.spring.io/spring-boot/docs/2.5.6/reference/htmlsingle/#howto-execute-liquibase-database-migrations-on-startup)
* [OpenFeign](https://docs.spring.io/spring-cloud-openfeign/docs/current/reference/html/)
* [Testcontainers](https://www.testcontainers.org/)
* [Spring Web](https://docs.spring.io/spring-boot/docs/2.5.6/reference/htmlsingle/#boot-features-developing-web-applications)
* [Redoc](https://github.com/Redocly/redoc)
* [Rqueue](https://github.com/sonus21/rqueue)
* [Spring Cloud](https://spring.io/projects/spring-cloud)
* [Awaitilly](http://www.awaitility.org)
* [Java Faker](http://com.github.javafaker)
* [Wiremock](http://wiremock.org/)
* [OpenApi](https://www.openapis.org/)

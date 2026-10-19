# ATVII - AutoBots

Continuação da ATVI: API para cadastro de clientes, documentos, telefones e endereços com os níveis de maturidade REST de Richardson. Desenvolvida com Java, Spring Boot, Spring Data JPA, Spring HATEOAS e H2.


## Como executar

É necessário ter JDK 17 ou superior e a variável `JAVA_HOME` apontando para a pasta do JDK. O Maven Wrapper está incluído; a primeira execução precisa de internet para baixar as dependências.

Dentro da pasta `automanager`, execute no PowerShell:

```powershell
.\mvnw.cmd spring-boot:run
```

A porta configurada é **8081**. Para escolher outra porta:

```powershell
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.arguments=--server.port=8082"
```

Acesse http://localhost:8081/ para descobrir os links da API ou http://localhost:8081/clientes para consultar os clientes. A raiz retorna JSON com links; não é uma interface gráfica. Mantenha o terminal aberto; para encerrar, pressione `Ctrl+C`.

Para executar os testes e gerar o JAR:

```powershell
.\mvnw.cmd clean verify
java -jar target/automanager-0.0.1-SNAPSHOT.jar
```

O banco é criado automaticamente na pasta `dados` e mantém os registros entre execuções.

## Rotas

URL base: `http://localhost:8081`. Envie os corpos das requisições com `Content-Type: application/json`.

| Método | Rota | Operação |
|---|---|---|
| GET | `/` | Descobrir as coleções da API |
| GET | `/clientes` | Listar clientes |
| GET | `/clientes/{id}` | Consultar cliente |
| POST | `/clientes` | Cadastrar cliente |
| PUT | `/clientes/{id}` | Atualizar dados pessoais |
| DELETE | `/clientes/{id}` | Excluir cliente e seus dados associados |
| GET | `/documentos` | Listar documentos |
| GET | `/documentos/{id}` | Consultar documento |
| POST | `/documentos?clienteId={clienteId}` | Cadastrar documento no cliente |
| PUT | `/documentos/{id}` | Atualizar documento |
| DELETE | `/documentos/{id}` | Excluir documento |
| GET | `/telefones` | Listar telefones |
| GET | `/telefones/{id}` | Consultar telefone |
| POST | `/telefones?clienteId={clienteId}` | Cadastrar telefone no cliente |
| PUT | `/telefones/{id}` | Atualizar telefone |
| DELETE | `/telefones/{id}` | Excluir telefone |
| GET | `/enderecos` | Listar endereços |
| GET | `/enderecos/{id}` | Consultar endereço |
| POST | `/enderecos?clienteId={clienteId}` | Cadastrar endereço no cliente |
| PUT | `/enderecos/{id}` | Atualizar endereço |
| DELETE | `/enderecos/{id}` | Excluir endereço |

As consultas `GET /documentos`, `GET /telefones` e `GET /enderecos` também aceitam `?clienteId={clienteId}` para filtrar os registros de um cliente. Um cliente inexistente retorna 404; um cliente existente sem registros retorna uma coleção vazia com 200.

Substitua `{id}` pelo ID do registro consultado e `{clienteId}` pelo ID do cliente proprietário. Utilize os IDs retornados pela API.

## Exemplos de JSON

### Cadastrar cliente — POST /clientes

Os IDs e a data de cadastro são gerados pelo servidor. É possível cadastrar apenas o nome ou enviar os dados associados juntos:

```json
{
  "nome": "Ana Silva",
  "nomeSocial": "Ana",
  "dataNascimento": "2000-05-15",
  "endereco": {
    "estado": "SP",
    "cidade": "Santos",
    "bairro": "Centro",
    "rua": "Rua das Flores",
    "numero": "100",
    "codigoPostal": "11000000",
    "informacoesAdicionais": "Apartamento 12"
  },
  "documentos": [
    { "tipo": "RG", "numero": "123456789" }
  ],
  "telefones": [
    { "ddd": "13", "numero": "999999999" }
  ]
}
```

### Atualizar cliente — PUT /clientes/{id}

```json
{
  "nome": "Ana Souza",
  "nomeSocial": "Ana",
  "dataNascimento": "2000-05-15"
}
```

Essa rota altera somente os dados pessoais. Para alterar os dados associados, use suas respectivas rotas.

### Documento — POST /documentos?clienteId={clienteId} ou PUT /documentos/{id}

```json
{
  "tipo": "RG",
  "numero": "987654321"
}
```

### Telefone — POST /telefones?clienteId={clienteId} ou PUT /telefones/{id}

```json
{
  "ddd": "11",
  "numero": "988887777"
}
```

### Endereço — POST /enderecos?clienteId={clienteId} ou PUT /enderecos/{id}

```json
{
  "estado": "SP",
  "cidade": "São Paulo",
  "bairro": "Centro",
  "rua": "Rua A",
  "numero": "20",
  "codigoPostal": "01001000",
  "informacoesAdicionais": "Casa"
}
```

Cada cliente pode ter um endereço. Se ele já tiver endereço, use PUT para atualizar. Nos PUTs, envie todos os campos que deseja manter: campos opcionais omitidos ficam nulos.

## Respostas

- `200`: consulta ou atualização realizada.
- `201`: cadastro criado, com o registro no corpo e sua URL no cabeçalho `Location`.
- `204`: exclusão realizada, sem corpo na resposta.
- `400`: dados inválidos.
- `404`: registro não encontrado.
- `409`: conflito, como número de documento repetido ou segundo endereço para o mesmo cliente.


## O que mudou na ATVII

| Nível RMM | Aplicação no projeto |
|---|---|
| 0 — HTTP | Comunicação por HTTP com representação JSON. |
| 1 — Recursos | URIs próprias: `/clientes/{id}`, `/documentos/{id}`, `/telefones/{id}` e `/enderecos/{id}`. |
| 2 — Métodos e semântica | GET consulta sem alterar dados; POST cadastra; PUT substitui os dados editáveis; DELETE exclui. Cadastro retorna 201 com Location, consulta/atualização retorna 200 e exclusão retorna 204. |
| 3 — HATEOAS | As respostas possuem links de navegação e podem expor as operações disponíveis por HAL-FORMS. |

Os níveis representam uma evolução: a aplicação usa os recursos dos níveis superiores, sem criar uma API separada para cada nível. O código inicial do professor usa Spring HATEOAS para adicionar links ao cliente; esta continuação aplica o recurso às quatro entidades. Consultas bem-sucedidas usam 200 (302 significa redirecionamento), e listas vazias continuam sendo recursos existentes, portanto retornam 200.

### Navegação com links (HAL)

Envie o cabeçalho `Accept: application/hal+json`. A raiz `/` possui links para as quatro coleções. Cada registro tem `self`, um link para sua coleção e um link para `inicio`. Documentos, telefones e endereços também apontam para o `cliente` proprietário.

As respostas das listagens passaram de arrays simples para objetos HAL com `_links` e registros dentro de `_embedded`, por exemplo `_embedded.clientes`. Nos registros individuais, os campos continuam no nível principal, acompanhados de `_links`. Isso é uma mudança no formato das respostas em relação à ATVI.

Exemplo de trecho de links retornados por um cliente:

```json
{
  "_links": {
    "self": { "href": "http://localhost:8081/clientes/1" },
    "clientes": { "href": "http://localhost:8081/clientes" },
    "documentos": { "href": "http://localhost:8081/documentos?clienteId=1" },
    "telefones": { "href": "http://localhost:8081/telefones?clienteId=1" },
    "endereco": { "href": "http://localhost:8081/enderecos/1" },
    "inicio": { "href": "http://localhost:8081/" }
  }
}
```

Os endereços dos links são gerados a partir dos controladores e da requisição; a porta não é fixada no código. Se o cliente ainda não tiver endereço, aparece `enderecos`, apontando para a coleção filtrada em que ele pode ser cadastrado. Se já tiver, aparece `endereco`, apontando para o registro existente.

### Operações disponíveis (HAL-FORMS)

Para receber também os métodos e campos aceitos, envie:

```text
Accept: application/prs.hal-forms+json
```

O corpo inclui `_templates`, além de `_links`. O template `default` de um cliente informa PUT e os campos editáveis; `excluir` informa DELETE. As relações de documentos e telefones anunciam seus cadastros com POST e o destino em `target`. A criação de endereço só é anunciada enquanto o cliente não possui um.

No Postman, use a aba Headers para selecionar o formato. Para POST e PUT, continue enviando `Content-Type: application/json`. Envie somente os dados do cadastro/atualização: não copie `_links` ou `_templates` para o corpo. IDs são gerados pelo servidor e não devem ser enviados no cadastro.

### Roteiro para conferir

1. Inicie a aplicação e faça GET `/`.
2. Siga o link `clientes` e cadastre o JSON de exemplo com POST nessa URL.
3. Confira o status 201 e o cabeçalho Location, que aponta para o cliente criado.
4. Faça GET no link `self` e siga os links de documentos, telefones e endereço.
5. Repita o GET com Accept de HAL-FORMS e confira `_templates`.
6. Atualize o cliente com PUT no seu link `self`; os dados associados permanecem vinculados.
7. Exclua o endereço usando DELETE no link do endereço. Consulte o cliente novamente: agora ele oferece a relação `enderecos`, permitindo cadastrar outro endereço.
8. Exclua o cliente com DELETE no link `self`. A resposta é 204 e seus registros associados são removidos. Consultar o cliente excluído retorna 404.

Os erros usam `application/problem+json`, com campos como `status`, `title` e `detail`; erros de validação incluem `campos`. Método não permitido retorna 405, formato de entrada incompatível retorna 415 e formato de resposta não suportado retorna 406.

### Organização

- `entidades`: dados persistidos no banco, mantidos da ATVI.
- `repositorios` e `servicos`: consultas, regras de negócio e transações.
- `controles`: rotas e códigos HTTP.
- `representacoes`: montagem dos links com `EntityModel`, `CollectionModel` e `WebMvcLinkBuilder`, sem persistir links no banco.
- `configuracao`: habilitação de HAL e HAL-FORMS.

O teste básico original continua em `src/test`. A implementação também foi verificada com requisições HTTP em banco temporário, abrangendo os CRUDs, links, formulários, erros e exclusão em cascata.


# Industrial Production Optimizer

Aplicação full stack para controle de produção industrial, permitindo o cadastro de matérias-primas, produtos, composição de produtos e cálculo da melhor combinação de produção com base no estoque disponível.

O objetivo do sistema é maximizar o valor total de venda dos produtos produzidos, respeitando as limitações de estoque das matérias-primas.

## Tecnologias utilizadas

### Backend

- Java 21
- Spring Boot
- Spring Web
- Spring Data JPA
- Bean Validation
- PostgreSQL
- Flyway
- JUnit
- Mockito
- Swagger/OpenAPI

### Frontend

- Vue.js
- Vue Router
- Pinia
- Axios
- Vite

## Funcionalidades

- Cadastro, edição, listagem e exclusão de matérias-primas;
- Cadastro, edição, listagem e exclusão de produtos;
- Definição da composição de cada produto;
- Validação de regras de negócio;
- Cálculo automático do melhor plano de produção;
- Exibição do valor total de venda;
- Exibição das sobras de matérias-primas após o cálculo;
- Interface web integrada com a API REST.

## Estrutura do projeto

```text
industrial-production-optimizer/
├── backend/
│   └── API REST desenvolvida com Spring Boot
├── frontend/
│   └── Interface web desenvolvida com Vue.js
└── README.md
```

## Regras principais

### Matérias-primas

Cada matéria-prima possui:

- Código;
- Nome;
- Quantidade disponível em estoque;
- Unidade de medida.

### Produtos

Cada produto possui:

- Código;
- Nome;
- Valor de venda;
- Composição com uma ou mais matérias-primas.

A composição define a quantidade necessária de cada matéria-prima para produzir uma unidade do produto.

### Plano de produção

O sistema calcula a melhor combinação de produtos a serem produzidos com base no estoque disponível.

O cálculo considera:

- Estoque atual de cada matéria-prima;
- Quantidade necessária de matéria-prima por produto;
- Valor de venda de cada produto;
- Restrições de estoque;
- Maximização do valor total de venda.

## Como rodar o backend

### Pré-requisitos

- Java 21
- Maven
- PostgreSQL

### Banco de dados

Crie um banco PostgreSQL com o nome:

```text
industrial_production_optimizer
```

Configure o arquivo `backend/src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/industrial_production_optimizer
spring.datasource.username=postgres
spring.datasource.password=SUA_SENHA_DO_POSTGRES

spring.jpa.hibernate.ddl-auto=validate
spring.flyway.enabled=true

server.port=8080
```

### Executar a aplicação

Entre na pasta do backend:

```bash
cd backend
```

No Linux ou macOS, execute:

```bash
./mvnw spring-boot:run
```

No Windows, execute:

```powershell
mvnw.cmd spring-boot:run
```

A API ficará disponível em <http://localhost:8080>.

### Swagger

A documentação da API pode ser acessada em:

- <http://localhost:8080/swagger-ui.html>
- <http://localhost:8080/swagger-ui/index.html>

## Como rodar o frontend

### Pré-requisitos

- Node.js
- npm

Entre na pasta do frontend:

```bash
cd frontend
```

Instale as dependências:

```bash
npm install
```

Execute o projeto:

```bash
npm run dev
```

O frontend ficará disponível em <http://localhost:5173>.

## Endpoints principais

### Matérias-primas

```http
GET    /api/raw-materials
POST   /api/raw-materials
GET    /api/raw-materials/{id}
PUT    /api/raw-materials/{id}
DELETE /api/raw-materials/{id}
```

### Produtos

```http
GET    /api/products
POST   /api/products
GET    /api/products/{id}
PUT    /api/products/{id}
DELETE /api/products/{id}
```

### Plano de produção

```http
POST /api/production-plans/calculate
```

## Exemplo de produto

```json
{
  "code": "PRD-001",
  "name": "Mesa Industrial",
  "salePrice": 850.00,
  "compositions": [
    {
      "rawMaterialId": 1,
      "requiredQuantity": 15.000
    },
    {
      "rawMaterialId": 2,
      "requiredQuantity": 20.000
    }
  ]
}
```

## Exemplo de resposta do plano de produção

```json
{
  "items": [
    {
      "productCode": "PRD-001",
      "productName": "Mesa Industrial",
      "quantity": 12,
      "unitSalePrice": 850.00,
      "totalSaleValue": 10200.00
    }
  ],
  "totalProductionUnits": 12,
  "totalSaleValue": 10200.00,
  "remainingRawMaterials": [
    {
      "rawMaterialCode": "MP-001",
      "rawMaterialName": "Aço",
      "initialQuantity": 500.000,
      "usedQuantity": 180.000,
      "remainingQuantity": 320.000,
      "unit": "KG"
    }
  ]
}
```

## Testes

Para executar os testes do backend, entre na pasta correspondente:

```bash
cd backend
```

No Linux ou macOS, execute:

```bash
./mvnw test
```

No Windows, execute:

```powershell
mvnw.cmd test
```

Os testes validam principalmente:

- Cadastro de matérias-primas;
- Cadastro de produtos;
- Validações de regras de negócio;
- Cálculo do plano de produção;
- Cenários de estoque insuficiente;
- Escolha da combinação com maior valor total de venda.

## Observações

- O cálculo do plano de produção é uma simulação e não altera o estoque das matérias-primas.
- A exclusão de matérias-primas vinculadas a produtos é bloqueada para manter a integridade dos dados.
- O backend possui validações de negócio e mensagens de erro em português.

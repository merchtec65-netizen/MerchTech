# MerchTech

Sistema web desenvolvido em Java com Servlets (Jakarta) para gerenciamento de promotores e mercados.  
A aplicação consome dados de um banco Firebird e exibe informações via interface web (HTML/CSS/JS).

---

## Tecnologias utilizadas

- Java 21 (JDK 21)
- Apache Tomcat 11
- Jakarta Servlet (Servlet API 6+)
- Eclipse IDE
- DBeaver
- HTML5
- CSS3
- JavaScript
- JDBC
- Firebird SQL
- Gson

---

## Estrutura do projeto

```

src/

├── controller/
│   ├── Controller.java
│   ├── MercadoController.java
│

├── model/
│   ├── DAO.java
│   ├── JbMercado.java
│   ├── JbUsuario.java
│
WebContent/
├── promotor/
│   ├── promotor.html
│   ├── mercados.html
├── IMAGEM/

```

---

## Funcionalidades

- Login de usuários (promotor/supervisor)
- Listagem de mercados cadastrados
- Filtragem de mercados ativos
- Interface com tema escuro
- Consumo de API via fetch

---

## Endpoints

### GET /mercados

Retorna lista de mercados em JSON.

---

## Requisitos para execução

### Servidor
- Apache Tomcat 11

### Java
- JDK 21 configurado no Eclipse

### Banco de dados
- Firebird Server ativo
- Banco .FDB configurado localmente

### Ferramenta opcional
- DBeaver

### Driver JDBC
- Jaybird (Firebird JDBC)

```

org.firebirdsql.jdbc.FBDriver

```

---

## Como executar

```

1. Importar o projeto no Eclipse
2. Configurar Apache Tomcat 11
3. Selecionar JDK 21
4. Iniciar Firebird Server
5. Rodar no Tomcat
6. Acessar [http://localhost:8080/MerchTech/](http://localhost:8080/MerchTech/)

```

---

## Problemas comuns

- 404 em /mercados → servlet não mapeado
- Lista vazia → erro no DAO ou conexão com banco
- Fetch não funciona → URL incorreta
- Erro no Tomcat 11 → uso de javax em vez de jakarta

---

## Observações

- Projeto sem Spring Boot
- Arquitetura baseada em Servlets + DAO
- Comunicação via JSON
```

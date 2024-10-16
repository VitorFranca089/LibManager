# 📚 LibManager 📚

**LibManager** é um sistema de gerenciamento de biblioteca desenvolvido em Java. Ele permite o controle de usuários, empréstimos de livros e gerencia o status de devolução, utilizando uma interface em linha de comando para bibliotecários e usuários. O sistema é um estudo da utilização do java puro com a biblioteca JDBC, com o intuito de se utilizar apenas SQL e Java na prática.

## ✔️ Funcionalidades

- **Gerenciamento de usuários**:
    - Cadastro de usuários comuns e bibliotecários.
    - Login de usuários utilizando username e senha.
    - Atribuição automática de username para novos usuários.

- **Gerenciamento de livros**:
    - Cadastro, atualização e remoção de livros.
    - Controle de disponibilidade dos livros.

- **Empréstimos de livros**:
    - Registro de novos empréstimos com data de vencimento.
    - Devolução de livros e atualização do status.
    - Verificação automática de empréstimos vencidos com status **OVERDUE**.

## ⚙️ Tecnologias Utilizadas

- **Linguagem**: Java.
- **Banco de Dados**: MySQL.
- **ORM**: JDBC para integração com o banco de dados.

## 🗂️ Estrutura do Projeto

- **Controller**: Responsável por controlar o fluxo da aplicação e a interação entre o serviço e a interface do usuário.
- **Service**: Camada de lógica de negócios, validação e processamento.
- **DAO (Data Access Object)**: Camada responsável pela interação direta com o banco de dados.
- **Model/DTO**: Representação dos dados das entidades (ex: `User`, `Book`, `Loan`).
- **Utils**: Funções auxiliares como entrada de dados e formatação.

## ❗ Instalação e Configuração

### Pré-requisitos

- **Java 17+**
- **MySQL**
- **Maven**

### Configurando o Banco de Dados

Antes de executar o Flyway para configurar o banco de dados, você precisa garantir que o banco de dados chamado `libmanager` está criado no MySQL. Para fazer isso:

1. Acesse o MySQL no terminal:
    ```bash
    mysql -u root -p
    ```

2. Crie o banco de dados:
    ```sql
    CREATE DATABASE libmanager;
    ```

3. Agora você pode executar o Flyway que irá aplicar as migrações automaticamente ao banco de dados `libmanager`.

### Executando o Projeto

1. Clone este repositório:
    ```bash
    git clone https://github.com/VitorFranca089/LibManager.git
    ```

2. Abra na sua IDE.

3. Execute o projeto.

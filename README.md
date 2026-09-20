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

- **Java 21+**
- **Maven**
- **MySQL** (rodando localmente **ou** via Docker — recomendado)

### Configurando o Banco de Dados

O projeto usa Flyway para aplicar as migrações automaticamente na primeira execução, então só é preciso garantir que exista um banco de dados chamado `libmanager` acessível.

**Opção A — via Docker (recomendado, mais rápido):**

```bash
docker run -d --name libmanager-mysql \
  -e MYSQL_ROOT_PASSWORD=libmanager \
  -e MYSQL_DATABASE=libmanager \
  -p 3306:3306 \
  mysql:8.3
```

Esse comando já sobe o MySQL e cria o banco `libmanager` automaticamente. Nas próximas vezes, basta `docker start libmanager-mysql`.

**Opção B — MySQL instalado localmente:**

1. Acesse o MySQL no terminal:
    ```bash
    mysql -u root -p
    ```

2. Crie o banco de dados:
    ```sql
    CREATE DATABASE libmanager;
    ```

### Variáveis de Ambiente

A aplicação lê os dados de conexão do banco via variáveis de ambiente:

| Variável      | Exemplo (usando a Opção A acima)       |
|---------------|-----------------------------------------|
| `DB_URL`      | `jdbc:mysql://localhost:3306/libmanager` |
| `DB_USERNAME` | `root`                                   |
| `DB_PASSWORD` | `libmanager`                             |

Ajuste os valores caso esteja usando outro usuário/senha/host.

### Executando o Projeto

1. Clone este repositório:
    ```bash
    git clone https://github.com/VitorFranca089/LibManager.git
    cd LibManager
    ```

2. Rode o script incluso, que compila o projeto e já executa com as variáveis de ambiente padrão (as mesmas da tabela acima):
    ```bash
    ./run.sh
    ```

   Isso vai aplicar as migrações do Flyway automaticamente e abrir o menu interativo no terminal.

   Para usar outras credenciais de banco, basta sobrescrever as variáveis antes de rodar:
    ```bash
    DB_URL=jdbc:mysql://localhost:3306/libmanager DB_USERNAME=root DB_PASSWORD=outrasenha ./run.sh
    ```

3. Alternativamente, abra o projeto na sua IDE (configure as variáveis de ambiente na Run Configuration) e execute a classe `Main`.

### Login padrão

A migração inicial já cadastra um usuário bibliotecário para o primeiro acesso:

- **Usuário:** `admin`
- **Senha:** `123`

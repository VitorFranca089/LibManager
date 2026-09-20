#!/usr/bin/env python3
"""Gerador do relatório de auditoria de segurança do LibManager.

Regenerar sem instalar nada no host, usando um container Python isolado
(rodando com o UID/GID do usuário atual para não gerar arquivos do root):

    docker run --rm --user "$(id -u):$(id -g)" -e HOME=/tmp \
        -v "$(pwd)":/work -w /work python:3.12-slim bash -c \
        'pip install --no-cache-dir --user -q -r requirements.txt && \
         PYTHONPATH=/tmp/.local/lib/python3.12/site-packages python generate_report.py'

Saída: relatorio-auditoria-seguranca.pdf (nesta mesma pasta).
"""

import io
import os

import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt

from reportlab.lib import colors
from reportlab.lib.enums import TA_CENTER, TA_JUSTIFY
from reportlab.lib.pagesizes import A4
from reportlab.lib.units import cm
from reportlab.platypus import (
    SimpleDocTemplate, Paragraph, Spacer, Table, TableStyle, Image,
    PageBreak, HRFlowable, KeepTogether, ListFlowable, ListItem
)
from reportlab.lib.styles import getSampleStyleSheet, ParagraphStyle

# ---------------------------------------------------------------------------
# Dados da auditoria
# ---------------------------------------------------------------------------

PROJECT_NAME = "LibManager"
REPO_URL = "https://github.com/VitorFranca089/LibManager"
REPORT_DATE = "20/09/2026"
GROUP_MEMBERS = [
    "Vitor Miguel Mendes de França",
    "Daniel Carrapeiro Mendes",
    "Anita Felicio dos Santos",
    "Fábia Aurelie Losija",
]

SEVERITY_COLORS = {
    "Crítica": colors.HexColor("#B91C1C"),
    "Alta": colors.HexColor("#EA580C"),
    "Média": colors.HexColor("#D97706"),
    "Baixa": colors.HexColor("#2563EB"),
    "Informativa": colors.HexColor("#6B7280"),
    "Ponto forte": colors.HexColor("#059669"),
}

# Ordem de severidade para ordenação e gráficos
SEVERITY_ORDER = ["Crítica", "Alta", "Média", "Baixa", "Informativa"]

CATEGORIES = [
    "1. Isolamento de usuário",
    "2. Permissão no navegador",
    "3. IDOR",
    "4. Chaves/credenciais expostas",
    "5. Inputs sem tratamento (XSS)",
    "Boas práticas gerais",
]

# Achados confirmados (fraquezas)
FINDINGS = [
    {
        "id": "F1",
        "categoria": "1. Isolamento de usuário",
        "severidade": "Alta",
        "titulo": "Quebra de isolamento entre usuários na consulta de empréstimos",
        "arquivo": "src/main/java/io/github/vitorfranca089/libmanager/dao/LoanDAO.java",
        "linhas": "74-76",
        "trecho": (
            'String sql = "SELECT * FROM loans INNER JOIN books ON books.id=loans.id_book " +\n'
            '        "WHERE loans.id_user = ? AND loans.loan_status = \'BORROWED\' OR loans.loan_status = \'OVERDUE\' " +\n'
            '        "ORDER BY loans.id ASC";'
        ),
        "descricao": (
            "O método findLoansByUser() é chamado tanto pelo menu do usuário comum "
            "(\"Consultar empréstimos\", com o próprio usuário autenticado) quanto pelo "
            "bibliotecário. O SQL monta a cláusula WHERE sem parênteses entre as condições "
            "combinadas com AND e OR. Como AND tem precedência maior que OR em SQL, a "
            "condição é avaliada como "
            "(loans.id_user = ? AND loan_status = 'BORROWED') OR (loan_status = 'OVERDUE'), "
            "e não como o pretendido "
            "loans.id_user = ? AND (loan_status = 'BORROWED' OR loan_status = 'OVERDUE'). "
            "Na prática, todo empréstimo com status OVERDUE de QUALQUER usuário é retornado, "
            "independentemente do parâmetro id_user."
        ),
        "explorabilidade": (
            "Não exige nenhuma condição especial: qualquer usuário comum autenticado, ao "
            "acessar \"Empréstimos > Consultar empréstimos\" no seu próprio menu, já recebe "
            "na resposta os empréstimos atrasados (OVERDUE) de outros usuários — livro, "
            "datas de empréstimo/vencimento e ID do empréstimo — sem que o próprio usuário "
            "tenha relação com aquele empréstimo."
        ),
        "impacto": (
            "Vazamento de dados entre contas (violação de isolamento/tenant), classificável "
            "como Broken Access Control (OWASP A01:2021 / CWE-284, CWE-639). Expõe metadados "
            "de outros usuários (quais livros estão atrasados e desde quando) e demonstra que "
            "a camada de dados não garante isolamento por usuário de forma confiável, "
            "quebrando uma garantia básica de privacidade do sistema."
        ),
        "evidencia_extra": (
            "Verificado experimentalmente em ambiente de teste local (dados fictícios): "
            "usuário fictício 'userA' com um empréstimo OVERDUE do livro 'Livro X' e usuário "
            "fictício 'userB' com um empréstimo BORROWED do livro 'Livro Y'. Ao logar como "
            "'userB' e consultar \"Meus empréstimos\", a listagem retornada pela aplicação "
            "real incluiu o empréstimo atrasado de 'userA' junto com o próprio empréstimo de "
            "'userB'. A mesma consulta, reescrita apenas adicionando parênteses ao redor da "
            "condição OR, retornou corretamente somente o empréstimo do usuário filtrado. Os "
            "dados de teste foram removidos do banco após a verificação."
        ),
        "correcao": (
            "Adicionar parênteses para restaurar a precedência lógica pretendida:\n"
            "WHERE loans.id_user = ? AND (loans.loan_status = 'BORROWED' OR "
            "loans.loan_status = 'OVERDUE')"
        ),
    },
    {
        "id": "F2",
        "categoria": "4. Chaves/credenciais expostas",
        "severidade": "Crítica",
        "titulo": "Credencial de administrador padrão fraca versionada no código-fonte",
        "arquivo": "src/main/resources/db/migration/V005__insert-users-admin.sql",
        "linhas": "1-4",
        "trecho": (
            "INSERT INTO users\n"
            "    (name, username, password, address, phone, role)\n"
            "    VALUES\n"
            "    ('admin', 'admin', '123', '', '', 'LIBRARIAN');"
        ),
        "descricao": (
            "A migração Flyway V005 é aplicada automaticamente em toda instalação nova do "
            "projeto (DatabaseConfig.migrateDatabase(), chamada em Main.java antes de abrir o "
            "menu) e cria, de forma incondicional, um usuário com o papel de maior privilégio "
            "do sistema (LIBRARIAN), usuário 'admin' e senha '123'. Não há nenhum mecanismo "
            "que force a troca dessa senha no primeiro acesso, nem validação de startup que "
            "alerte ou bloqueie o uso da credencial padrão."
        ),
        "explorabilidade": (
            "Como o repositório é público, a credencial não é um segredo: está publicada no "
            "histórico do GitHub. Qualquer pessoa com acesso à aplicação (rodando localmente "
            "ou, futuramente, exposta em rede) pode logar diretamente como bibliotecário "
            "usando admin/123, sem qualquer etapa adicional."
        ),
        "impacto": (
            "Comprometimento total do papel administrativo (LIBRARIAN): gestão de usuários "
            "(criar, editar, resetar senha de qualquer usuário para a senha padrão — "
            "UserController.libToDefaultPasswordUser()), gestão de livros e de todos os "
            "empréstimos do sistema. CWE-798 (Use of Hard-coded Credentials) / CWE-1188 "
            "(Insecure Default Initialization)."
        ),
        "evidencia_extra": None,
        "correcao": (
            "Não versionar credenciais reais mesmo como 'valor de fábrica'. Alternativas: "
            "(a) gerar uma senha aleatória forte no primeiro start e exigir troca obrigatória "
            "no primeiro login; (b) ler a senha inicial de uma variável de ambiente sem valor "
            "padrão, falhando o startup se ausente; (c) adicionar validação que rejeite login "
            "com a senha padrão sem antes forçar a troca."
        ),
    },
    {
        "id": "F3",
        "categoria": "4. Chaves/credenciais expostas",
        "severidade": "Alta",
        "titulo": "Senhas armazenadas e comparadas em texto puro (sem hashing)",
        "arquivo": (
            "src/main/java/io/github/vitorfranca089/libmanager/dao/AuthDAO.java; "
            "src/main/java/io/github/vitorfranca089/libmanager/dao/UserDAO.java"
        ),
        "linhas": "AuthDAO.java:15,19 · UserDAO.java:94-110,127-143",
        "trecho": (
            "// AuthDAO.java:15\n"
            'String sql = "SELECT id, name, username, address, phone, role FROM users '
            'WHERE username = ? AND password = ?";\n\n'
            "// UserDAO.java:136 (verifyPassword)\n"
            "return rs.getString(1).equals(oldPass);"
        ),
        "descricao": (
            "Em nenhum ponto do código (AuthDAO.login, UserDAO.updateUserWithPass, "
            "UserDAO.toDefaultPassword, UserDAO.verifyPassword) há aplicação de hashing "
            "(BCrypt, Argon2, PBKDF2) sobre a senha. O login compara a senha em texto puro "
            "diretamente na cláusula WHERE do SQL, e a troca/verificação de senha faz o mesmo "
            "com String.equals()."
        ),
        "explorabilidade": (
            "Exige acesso de leitura ao banco de dados (dump, backup, ou — combinado com o "
            "achado F2 — acesso administrativo já obtido) para expor as senhas de todos os "
            "usuários sem necessidade de quebra de hash."
        ),
        "impacto": (
            "Qualquer vazamento de banco/backup expõe diretamente as senhas de todos os "
            "usuários em texto puro, permitindo reuso malicioso em outros sistemas "
            "(credential stuffing), já que reutilização de senha entre serviços é comum. "
            "CWE-256 (Plaintext Storage of a Password)."
        ),
        "evidencia_extra": None,
        "correcao": (
            "Adotar um algoritmo de hash de senha com salt (BCrypt/Argon2id), armazenar "
            "apenas o hash, comparar com verificação de hash (não com igualdade de string) e "
            "migrar as senhas existentes no próximo login bem-sucedido."
        ),
    },
]

# Achados de baixa prioridade / boas práticas (agrupáveis)
MINOR_FINDINGS = [
    {
        "id": "F4",
        "categoria": "Boas práticas gerais",
        "severidade": "Baixa",
        "titulo": "Ausência de política mínima de senha",
        "arquivo": "src/main/java/io/github/vitorfranca089/libmanager/util/InputUtils.java",
        "linhas": "11-18",
        "trecho": (
            "public static String getStringNotBlank(String prompt){\n"
            "    String response;\n"
            "    do{\n"
            "        System.out.println(prompt);\n"
            "        response = in.nextLine();\n"
            "    }while(response.isBlank());\n"
            "    return response;\n"
            "}"
        ),
        "descricao": (
            "getStringNotBlank(), usado para capturar a senha no autocadastro "
            "(UserController.singUp()) e na edição de perfil, só rejeita valores vazios. "
            "Não há verificação de tamanho mínimo nem de complexidade, permitindo senhas "
            "como '1' ou 'a'."
        ),
        "explorabilidade": "Facilita ataques de força bruta/dicionário caso o sistema seja exposto em rede.",
        "impacto": "Reduz a robustez geral da autenticação (CWE-521 - Weak Password Requirements).",
        "evidencia_extra": None,
        "correcao": "Exigir tamanho mínimo (ex: 8 caracteres) e, idealmente, verificar contra listas de senhas comuns.",
    },
    {
        "id": "F5",
        "categoria": "Boas práticas gerais",
        "severidade": "Informativa",
        "titulo": "Tratamento de exceções via printStackTrace()",
        "arquivo": "Diversos DAOs (ex.: AuthDAO.java:33-35, LoanDAO.java, BookDAO.java, UserDAO.java)",
        "linhas": "catch(SQLException e){ e.printStackTrace(); }",
        "trecho": (
            "}catch(SQLException e){\n"
            "    e.printStackTrace();\n"
            "}"
        ),
        "descricao": (
            "O tratamento padrão de SQLException em todas as camadas DAO apenas imprime o "
            "stack trace completo (incluindo, em alguns casos, fragmentos de SQL) na saída "
            "padrão, sem log estruturado nem tratamento diferenciado."
        ),
        "explorabilidade": (
            "Hoje sem impacto direto, pois a saída vai para o terminal local do próprio "
            "usuário (aplicação CLI de processo único). Torna-se relevante caso o projeto "
            "evolua para uma API web/servidor, onde esse tipo de saída costuma vazar para "
            "logs acessíveis ou, por engano, para a resposta ao cliente."
        ),
        "impacto": "Baixo no cenário atual; risco de vazamento de informação técnica interna (CWE-209) se a arquitetura mudar para modo servidor/rede.",
        "evidencia_extra": None,
        "correcao": "Substituir por logging estruturado (ex.: java.util.logging ou SLF4J) sem expor detalhes internos em qualquer saída voltada ao usuário final.",
    },
]

ALL_FINDINGS = FINDINGS + MINOR_FINDINGS

# Categorias não aplicáveis (com justificativa)
NOT_APPLICABLE = [
    {
        "categoria": "2. Permissão definida no navegador",
        "justificativa": (
            "O LibManager é uma aplicação Java de linha de comando, executada como um único "
            "processo confiável (sem cliente web, sem API HTTP e sem separação front/back). "
            "A restrição de menus por papel (MainController.librarianMenu / commonUserMenu, "
            "MainController.java:34-37) não é uma barreira de UI que possa ser contornada "
            "\"forjando uma requisição\" — não existe canal de rede entre uma camada de "
            "apresentação desconfiada e o backend. Todo o fluxo roda no mesmo processo, com o "
            "papel do usuário lido do banco de dados logo após a autenticação "
            "(AuthDAO.login), nunca fornecido pelo lado \"cliente\". Categoria não aplicável "
            "à stack atual."
        ),
    },
    {
        "categoria": "5. Inputs sem tratamento (XSS)",
        "justificativa": (
            "Não há renderização de HTML, JavaScript, templates de e-mail ou navegador "
            "envolvidos em nenhum ponto do sistema. Toda a entrada do usuário é lida via "
            "Scanner (InputUtils) e toda a saída é texto puro impresso via System.out no "
            "terminal (InterfaceUtils). Categoria não aplicável à stack atual (CLI Java "
            "puro)."
        ),
    },
]

STRENGTHS = [
    {
        "titulo": "Nenhuma ocorrência de SQL Injection",
        "evidencia": (
            "Todas as 17 consultas SQL do projeto (arquivos AuthDAO.java, UserDAO.java, "
            "BookDAO.java, LoanDAO.java) usam PreparedStatement com parâmetros via bind "
            "(setString/setInt/setTimestamp). Não há uso de Statement genérico nem "
            "concatenação de entrada do usuário diretamente em texto SQL — verificado por "
            "varredura em todo o pacote dao/."
        ),
    },
    {
        "titulo": "IDOR mitigado nos fluxos acessíveis ao usuário comum",
        "evidencia": (
            "Nenhum handler acessível ao papel COMMON aceita um ID de outro usuário informado "
            "por ele mesmo: LoanController.userLoanMenu() (linhas 108-116) e "
            "ProfileController.profileMenu() (linha 12, chamado com user.id() da sessão "
            "autenticada em MainController.java:36) sempre usam o objeto do usuário logado, "
            "nunca um ID arbitrário digitado. Todos os fluxos que recebem IDs de terceiros "
            "(UserController.getUserById, BookController.*, LoanController.libLoanRegister/"
            "libLoanReturn/libLoanQuery) só são alcançáveis dentro do menu exclusivo do papel "
            "LIBRARIAN (MainController.java:39-58), papel obtido do banco após a autenticação."
        ),
    },
    {
        "titulo": "Sem escalonamento de privilégio no autocadastro público",
        "evidencia": (
            "O fluxo de autocadastro acessível a qualquer pessoa não autenticada "
            "(UserController.singUp(), linhas 124-136, chamado a partir de "
            "MainController.startMenu() opção 2) fixa o papel como Role.COMMON diretamente "
            "no código-fonte, sem expor ao usuário nenhuma opção de escolher o papel "
            "LIBRARIAN. A opção de escolher o papel (UserController.libUserSingUp(), "
            "linha 52) só existe dentro do menu já restrito ao bibliotecário."
        ),
    },
]

# ---------------------------------------------------------------------------
# Comunicação ao mantenedor / issues (preenchido após criação real das issues)
# ---------------------------------------------------------------------------

MAINTAINER_COMM = {
    "mantenedor": "Vitor Miguel Mendes de França (autor e único mantenedor do repositório)",
    "canal": (
        "Comunicação direta ao mantenedor durante a própria sessão de auditoria (o "
        "mantenedor é integrante do grupo responsável por esta atividade), seguida do "
        "registro formal e rastreável através da abertura de Issues públicas no "
        "repositório GitHub do projeto."
    ),
    "data": REPORT_DATE,
    "evidencias": [
        ("https://github.com/VitorFranca089/LibManager/issues/1",
         "Issue 1 — F1, isolamento entre usuários"),
        ("https://github.com/VitorFranca089/LibManager/issues/2",
         "Issue 2 — F2/F3, credenciais"),
        ("https://github.com/VitorFranca089/LibManager/issues/3",
         "Issue 3 — F4/F5, hardening"),
    ],
}

ISSUES = [
    {
        "numero": 1,
        "titulo": "[Segurança] Isolamento quebrado: usuário comum vê empréstimos atrasados de outros usuários",
        "labels": "security, severidade-alta",
        "relacionados": ["F1"],
        "descricao": (
            "O método `LoanDAO.findLoansByUser()` monta a cláusula `WHERE` sem parênteses "
            "entre as condições `AND`/`OR`. Como `AND` tem precedência maior que `OR` em SQL, "
            "a condição vira `(id_user = ? AND status = 'BORROWED') OR (status = 'OVERDUE')` "
            "em vez de `id_user = ? AND (status = 'BORROWED' OR status = 'OVERDUE')`. Isso "
            "faz a consulta retornar os empréstimos **OVERDUE de todos os usuários**, "
            "independentemente do `id_user` filtrado — quebrando o isolamento entre contas."
        ),
        "evidencia": (
            "`src/main/java/io/github/vitorfranca089/libmanager/dao/LoanDAO.java:74-76`\n\n"
            "```java\n"
            'String sql = "SELECT * FROM loans INNER JOIN books ON books.id=loans.id_book " +\n'
            '        "WHERE loans.id_user = ? AND loans.loan_status = \'BORROWED\' OR loans.loan_status = \'OVERDUE\' " +\n'
            '        "ORDER BY loans.id ASC";\n'
            "```\n\n"
            "Verificado em ambiente de teste local com dois usuários fictícios "
            "(`userA` com empréstimo OVERDUE, `userB` com empréstimo BORROWED): ao logar "
            "como `userB` e consultar \"Meus empréstimos\", o empréstimo atrasado de `userA` "
            "aparece na listagem junto com o de `userB`."
        ),
        "impacto": (
            "Vazamento de dados entre contas de usuários (Broken Access Control — "
            "OWASP A01:2021, CWE-284/CWE-639). Qualquer usuário comum autenticado enxerga "
            "quais livros outros usuários têm atrasados, desde quando, e o ID interno desses "
            "empréstimos."
        ),
        "correcao": (
            "Adicionar parênteses para restaurar a precedência pretendida:\n\n"
            "```sql\n"
            "WHERE loans.id_user = ? AND (loans.loan_status = 'BORROWED' OR loans.loan_status = 'OVERDUE')\n"
            "```"
        ),
        "criterios": [
            "A consulta em LoanDAO.findLoansByUser() usa parênteses e só retorna empréstimos do id_user filtrado.",
            "Teste automatizado (unitário/integração) cobre o cenário: dois usuários, um com empréstimo OVERDUE e outro com BORROWED, validando que cada usuário só vê seus próprios empréstimos.",
            "Revisão manual confirmando que nenhuma outra query do projeto tem o mesmo padrão de AND/OR sem parênteses.",
        ],
    },
    {
        "numero": 2,
        "titulo": "[Segurança] Credencial admin padrão fraca (admin/123) e senhas em texto puro no banco",
        "labels": "security, severidade-critica",
        "relacionados": ["F2", "F3"],
        "descricao": (
            "Dois achados relacionados ao ciclo de vida de credenciais:\n\n"
            "1. A migração Flyway `V005__insert-users-admin.sql` cria, em toda instalação "
            "nova, um usuário `admin` com senha `123` e papel `LIBRARIAN` (privilégio "
            "máximo), sem forçar troca de senha no primeiro acesso.\n"
            "2. Nenhuma camada do projeto (`AuthDAO`, `UserDAO`) aplica hash sobre as senhas: "
            "elas são armazenadas e comparadas em texto puro (`WHERE ... password = ?`, "
            "`String.equals()`)."
        ),
        "evidencia": (
            "`src/main/resources/db/migration/V005__insert-users-admin.sql:1-4`\n\n"
            "```sql\n"
            "INSERT INTO users\n"
            "    (name, username, password, address, phone, role)\n"
            "    VALUES\n"
            "    ('admin', 'admin', '123', '', '', 'LIBRARIAN');\n"
            "```\n\n"
            "`src/main/java/io/github/vitorfranca089/libmanager/dao/AuthDAO.java:15`\n\n"
            "```java\n"
            'String sql = "SELECT id, name, username, address, phone, role FROM users '
            'WHERE username = ? AND password = ?";\n'
            "```"
        ),
        "impacto": (
            "A credencial padrão está publicada no histórico do repositório público, "
            "permitindo login administrativo imediato por qualquer pessoa com acesso à "
            "aplicação. Combinado com o armazenamento em texto puro, qualquer leitura do "
            "banco (dump/backup) expõe diretamente as senhas de todos os usuários "
            "(CWE-798, CWE-1188, CWE-256)."
        ),
        "correcao": (
            "- Remover a senha fixa da migração; gerar senha aleatória no primeiro start ou "
            "lê-la de variável de ambiente obrigatória, forçando troca no primeiro login.\n"
            "- Adotar hash de senha com salt (BCrypt/Argon2id) em `AuthDAO`/`UserDAO`, "
            "migrando os registros existentes no próximo login bem-sucedido."
        ),
        "criterios": [
            "Nenhuma senha em texto puro é inserida via migration; o setup inicial força definição/troca de senha.",
            "AuthDAO.login() e UserDAO passam a comparar/armazenar hash (BCrypt/Argon2id), nunca a senha em texto puro.",
            "Senhas já existentes no banco são migradas (hash aplicado) de forma transparente no próximo login.",
        ],
    },
    {
        "numero": 3,
        "titulo": "[Segurança] Hardening: política mínima de senha e logging de exceções JDBC",
        "labels": "security, severidade-baixa",
        "relacionados": ["F4", "F5"],
        "descricao": (
            "Dois itens de baixa severidade/hardening, agrupados por serem triviais de "
            "corrigir e não exigirem issue individual:\n\n"
            "1. `InputUtils.getStringNotBlank()` só rejeita valores vazios — não há tamanho "
            "mínimo nem verificação de complexidade para senhas.\n"
            "2. Todo o tratamento de `SQLException` nas classes DAO usa apenas "
            "`e.printStackTrace()`, sem logging estruturado."
        ),
        "evidencia": (
            "`src/main/java/io/github/vitorfranca089/libmanager/util/InputUtils.java:11-18` "
            "(sem verificação de tamanho mínimo)\n\n"
            "Padrão repetido em `AuthDAO.java`, `UserDAO.java`, `BookDAO.java`, "
            "`LoanDAO.java`:\n\n"
            "```java\n"
            "}catch(SQLException e){\n"
            "    e.printStackTrace();\n"
            "}\n"
            "```"
        ),
        "impacto": (
            "Impacto baixo no cenário atual (aplicação CLI local), mas reduz a robustez da "
            "autenticação e pode vazar detalhes internos via stack trace caso o projeto "
            "evolua para um servidor/API exposto em rede (CWE-521, CWE-209)."
        ),
        "correcao": (
            "- Exigir tamanho mínimo de senha (ex.: 8 caracteres) em `InputUtils`/fluxo de "
            "cadastro e troca de senha.\n"
            "- Substituir `e.printStackTrace()` por logging estruturado (SLF4J/"
            "java.util.logging), sem expor detalhes internos em saídas voltadas ao "
            "usuário final."
        ),
        "criterios": [
            "Cadastro/troca de senha rejeita senhas abaixo de um tamanho mínimo definido.",
            "Exceções JDBC passam a ser tratadas por um logger estruturado em vez de printStackTrace().",
        ],
    },
]

# ---------------------------------------------------------------------------
# Estilos
# ---------------------------------------------------------------------------

styles = getSampleStyleSheet()
styles.add(ParagraphStyle(name="CoverTitle", fontName="Helvetica-Bold", fontSize=24,
                           leading=30, alignment=TA_CENTER, textColor=colors.HexColor("#111827"),
                           spaceAfter=6))
styles.add(ParagraphStyle(name="CoverSubtitle", fontName="Helvetica", fontSize=13,
                           leading=18, alignment=TA_CENTER, textColor=colors.HexColor("#374151")))
styles.add(ParagraphStyle(name="H1", fontName="Helvetica-Bold", fontSize=16, leading=20,
                           textColor=colors.HexColor("#111827"), spaceBefore=14, spaceAfter=8))
styles.add(ParagraphStyle(name="H2", fontName="Helvetica-Bold", fontSize=12.5, leading=16,
                           textColor=colors.HexColor("#1F2937"), spaceBefore=10, spaceAfter=6))
styles.add(ParagraphStyle(name="Body", fontName="Helvetica", fontSize=9.3, leading=13.3,
                           alignment=TA_JUSTIFY, textColor=colors.HexColor("#1F2937")))
styles.add(ParagraphStyle(name="BodySmall", fontName="Helvetica", fontSize=8.3, leading=11.5,
                           alignment=TA_JUSTIFY, textColor=colors.HexColor("#374151")))
styles.add(ParagraphStyle(name="CodeBlock", fontName="Courier", fontSize=7.6, leading=10.2,
                           textColor=colors.HexColor("#111827"), backColor=colors.HexColor("#F3F4F6"),
                           borderPadding=(6, 6, 6, 6), leftIndent=2))
styles.add(ParagraphStyle(name="Label", fontName="Helvetica-Bold", fontSize=8.8, leading=12,
                           textColor=colors.HexColor("#111827")))
styles.add(ParagraphStyle(name="TableCell", fontName="Helvetica", fontSize=8, leading=10.5,
                           textColor=colors.HexColor("#1F2937")))
styles.add(ParagraphStyle(name="TableHeaderCell", fontName="Helvetica-Bold", fontSize=8.4,
                           leading=11, textColor=colors.white))
styles.add(ParagraphStyle(name="IssueBody", fontName="Courier", fontSize=7.4, leading=10.5,
                           textColor=colors.HexColor("#111827")))


def severity_chip(sev):
    color = SEVERITY_COLORS.get(sev, colors.grey)
    t = Table([[sev]], colWidths=[2.7 * cm])
    t.setStyle(TableStyle([
        ("BACKGROUND", (0, 0), (-1, -1), color),
        ("TEXTCOLOR", (0, 0), (-1, -1), colors.white),
        ("FONTNAME", (0, 0), (-1, -1), "Helvetica-Bold"),
        ("FONTSIZE", (0, 0), (-1, -1), 7.6),
        ("ALIGN", (0, 0), (-1, -1), "CENTER"),
        ("VALIGN", (0, 0), (-1, -1), "MIDDLE"),
        ("TOPPADDING", (0, 0), (-1, -1), 3),
        ("BOTTOMPADDING", (0, 0), (-1, -1), 3),
    ]))
    return t


# ---------------------------------------------------------------------------
# Gráficos
# ---------------------------------------------------------------------------

def build_charts(tmpdir):
    plt.rcParams.update({"font.size": 9, "font.family": "DejaVu Sans"})

    # Donut por severidade
    counts = {s: 0 for s in SEVERITY_ORDER}
    for f in ALL_FINDINGS:
        counts[f["severidade"]] += 1
    labels = [s for s in SEVERITY_ORDER if counts[s] > 0]
    values = [counts[s] for s in labels]
    chip_colors = [SEVERITY_COLORS[s].hexval().replace("0x", "#") for s in labels]

    fig, ax = plt.subplots(figsize=(4.4, 3.6), dpi=170)
    wedges, _ = ax.pie(values, colors=chip_colors, startangle=90,
                        wedgeprops=dict(width=0.42, edgecolor="white", linewidth=2))
    ax.text(0, 0, str(sum(values)), ha="center", va="center", fontsize=22, fontweight="bold",
            color="#111827")
    ax.text(0, -0.22, "achados", ha="center", va="center", fontsize=9, color="#6B7280")
    ax.legend(wedges, [f"{s} ({counts[s]})" for s in labels], loc="center left",
               bbox_to_anchor=(1.0, 0.5), frameon=False, fontsize=8.5)
    ax.set_title("Achados por severidade", fontsize=11, fontweight="bold", color="#111827", pad=10)
    fig.tight_layout()
    donut_path = os.path.join(tmpdir, "donut_severidade.png")
    fig.savefig(donut_path, bbox_inches="tight", transparent=False, facecolor="white")
    plt.close(fig)

    # Barras por categoria
    cat_counts = {c: 0 for c in CATEGORIES}
    for f in ALL_FINDINGS:
        cat_counts[f["categoria"]] += 1
    cats = CATEGORIES
    vals = [cat_counts[c] for c in cats]
    bar_colors = []
    for c, v in zip(cats, vals):
        if v == 0:
            bar_colors.append("#D1D5DB")
        else:
            bar_colors.append("#EA580C" if "credenciais" in c.lower() or "isolamento" in c.lower()
                               else "#D97706" if c == "Boas práticas gerais" else "#2563EB")

    fig2, ax2 = plt.subplots(figsize=(6.4, 3.6), dpi=170)
    short_labels = [c.split(". ", 1)[-1] if ". " in c else c for c in cats]
    bars = ax2.barh(short_labels, vals, color=bar_colors)
    ax2.invert_yaxis()
    ax2.set_xlabel("Nº de achados")
    ax2.set_title("Achados por categoria auditada", fontsize=11, fontweight="bold", color="#111827")
    ax2.spines["top"].set_visible(False)
    ax2.spines["right"].set_visible(False)
    max_v = max(vals) if max(vals) > 0 else 1
    ax2.set_xlim(0, max_v + 1)
    ax2.xaxis.set_major_locator(plt.MaxNLocator(integer=True))
    for bar, v in zip(bars, vals):
        ax2.text(bar.get_width() + 0.05, bar.get_y() + bar.get_height() / 2, str(v),
                  va="center", fontsize=8.5, color="#111827")
    fig2.tight_layout()
    bar_path = os.path.join(tmpdir, "barras_categoria.png")
    fig2.savefig(bar_path, bbox_inches="tight", transparent=False, facecolor="white")
    plt.close(fig2)

    return donut_path, bar_path


# ---------------------------------------------------------------------------
# Documento
# ---------------------------------------------------------------------------

REPORT_TITLE = f"Relatório de Auditoria de Segurança — {PROJECT_NAME}"


def header_footer(canvas, doc):
    canvas.saveState()
    canvas.setFont("Helvetica", 7.5)
    canvas.setFillColor(colors.HexColor("#6B7280"))
    canvas.drawString(2 * cm, 1.3 * cm, REPORT_TITLE)
    canvas.drawRightString(A4[0] - 2 * cm, 1.3 * cm, f"Página {doc.page}")
    canvas.setStrokeColor(colors.HexColor("#E5E7EB"))
    canvas.line(2 * cm, 1.55 * cm, A4[0] - 2 * cm, 1.55 * cm)
    canvas.restoreState()


def build_pdf(output_path):
    tmpdir = os.path.dirname(os.path.abspath(output_path))
    donut_path, bar_path = build_charts(tmpdir)

    doc = SimpleDocTemplate(
        output_path, pagesize=A4,
        leftMargin=2 * cm, rightMargin=2 * cm, topMargin=2 * cm, bottomMargin=2 * cm,
        title=REPORT_TITLE, author=", ".join(GROUP_MEMBERS),
    )

    story = []

    # ---- Capa ----
    story.append(Spacer(1, 1.6 * cm))
    story.append(Paragraph(REPORT_TITLE, styles["CoverTitle"]))
    story.append(Spacer(1, 0.4 * cm))
    story.append(Paragraph(f"Data do relatório: {REPORT_DATE}", styles["CoverSubtitle"]))
    story.append(Paragraph(f"Repositório: {REPO_URL}", styles["CoverSubtitle"]))
    story.append(Spacer(1, 1.2 * cm))

    scope_table_data = [
        [Paragraph("<b>Escopo auditado</b>", styles["Label"]),
         Paragraph(
             "Código-fonte completo do repositório LibManager (branch main), incluindo "
             "camadas Controller, Service, DAO, Model/DTO, utilitários, migrações SQL "
             "(Flyway) e configuração de conexão com banco de dados. Auditoria estática de "
             "código-fonte com verificação dinâmica pontual (execução local em ambiente "
             "isolado com dados fictícios) para confirmar o achado principal.",
             styles["Body"])],
        [Paragraph("<b>Stack detectada</b>", styles["Label"]),
         Paragraph(
             "Java 21+ (compilado/testado com JDK 25), aplicação de linha de comando "
             "(sem frontend web), JDBC puro (sem ORM) via mysql-connector-j, MySQL 8.x, "
             "migrações de schema com Flyway. Autenticação própria (usuário/senha na tabela "
             "users), sem framework de autorização (ex.: Spring Security). Empacotamento "
             "Maven.", styles["Body"])],
        [Paragraph("<b>Metodologia</b>", styles["Label"]),
         Paragraph(
             "Aplicação das cinco categorias do prompt de auditoria de segurança, adaptadas "
             "à stack detectada: (1) isolamento de usuário mapeado para os filtros manuais "
             "por id_user nas queries JDBC, já que não há RLS nem middleware de tenant; "
             "(2) permissão definida no navegador mapeada para a fronteira entre menu/UI e "
             "camada de serviço — não aplicável por não existir separação cliente/servidor; "
             "(3) IDOR mapeado para todo handler que recebe um ID (usuário, livro, "
             "empréstimo) via entrada do usuário; (4) chaves/credenciais expostas revisado "
             "em código-fonte, migrações SQL e configuração; (5) XSS/inputs sem tratamento "
             "não aplicável por não existir renderização de HTML/navegador. Revisão "
             "arquivo por arquivo de todo o pacote io.github.vitorfranca089.libmanager, "
             "sem amostragem.", styles["Body"])],
        [Paragraph("<b>Grupo responsável</b>", styles["Label"]),
         Paragraph("<br/>".join(GROUP_MEMBERS), styles["Body"])],
    ]
    scope_table = Table(scope_table_data, colWidths=[3.6 * cm, 12.1 * cm])
    scope_table.setStyle(TableStyle([
        ("VALIGN", (0, 0), (-1, -1), "TOP"),
        ("BOX", (0, 0), (-1, -1), 0.6, colors.HexColor("#E5E7EB")),
        ("INNERGRID", (0, 0), (-1, -1), 0.6, colors.HexColor("#E5E7EB")),
        ("BACKGROUND", (0, 0), (0, -1), colors.HexColor("#F9FAFB")),
        ("LEFTPADDING", (0, 0), (-1, -1), 8),
        ("RIGHTPADDING", (0, 0), (-1, -1), 8),
        ("TOPPADDING", (0, 0), (-1, -1), 8),
        ("BOTTOMPADDING", (0, 0), (-1, -1), 8),
    ]))
    story.append(scope_table)

    story.append(Spacer(1, 0.5 * cm))
    story.append(Paragraph(
        "Finalidade educativa e responsável: este relatório documenta uma auditoria de "
        "segurança autorizada (projeto de autoria do próprio grupo), com o objetivo de "
        "ajudar a corrigir problemas reais e melhorar a segurança do sistema. Nenhum dado "
        "real de terceiros foi acessado, copiado ou alterado; toda a verificação dinâmica "
        "foi feita com contas e dados fictícios em ambiente local isolado, removidos ao "
        "final dos testes.", styles["BodySmall"]))
    story.append(PageBreak())

    # ---- Resumo executivo ----
    story.append(Paragraph("Resumo executivo", styles["H1"]))

    total = len(ALL_FINDINGS)
    counts = {s: sum(1 for f in ALL_FINDINGS if f["severidade"] == s) for s in SEVERITY_ORDER}
    summary_line = " · ".join(f"{s}: {counts[s]}" for s in SEVERITY_ORDER if counts[s] > 0)
    story.append(Paragraph(
        f"Foram identificados <b>{total} achados</b> ({summary_line}), cobrindo as cinco "
        f"categorias do prompt de auditoria (duas não aplicáveis à stack, justificadas na "
        f"seção correspondente) e 3 pontos fortes confirmados. Uma vulnerabilidade "
        f"<b>Crítica</b> (F2) e uma <b>Alta</b> (F1) foram confirmadas com evidência de "
        f"exploração — a F1 inclusive reproduzida ponta a ponta na aplicação real com dados "
        f"fictícios.", styles["Body"]))
    story.append(Spacer(1, 0.3 * cm))

    charts_table = Table([[Image(donut_path, width=8.2 * cm, height=6.6 * cm),
                            Image(bar_path, width=8.6 * cm, height=6.6 * cm)]],
                          colWidths=[8.4 * cm, 8.8 * cm])
    charts_table.setStyle(TableStyle([("VALIGN", (0, 0), (-1, -1), "TOP")]))
    story.append(charts_table)
    story.append(PageBreak())

    # ---- Pontos fortes ----
    story.append(Paragraph("Pontos fortes (controles verificados)", styles["H1"]))
    for s in STRENGTHS:
        row = Table([[severity_chip("Ponto forte"),
                       Paragraph(f"<b>{s['titulo']}</b><br/>{s['evidencia']}", styles["Body"])]],
                     colWidths=[2.9 * cm, 12.8 * cm])
        row.setStyle(TableStyle([
            ("VALIGN", (0, 0), (-1, -1), "TOP"),
            ("BOX", (0, 0), (-1, -1), 0.5, colors.HexColor("#D1FAE5")),
            ("BACKGROUND", (0, 0), (-1, -1), colors.HexColor("#F0FDF4")),
            ("LEFTPADDING", (0, 0), (-1, -1), 8), ("RIGHTPADDING", (0, 0), (-1, -1), 8),
            ("TOPPADDING", (0, 0), (-1, -1), 8), ("BOTTOMPADDING", (0, 0), (-1, -1), 8),
        ]))
        story.append(row)
        story.append(Spacer(1, 0.3 * cm))

    story.append(Spacer(1, 0.2 * cm))
    story.append(Paragraph("Categorias não aplicáveis à stack", styles["H2"]))
    for na in NOT_APPLICABLE:
        story.append(Paragraph(f"<b>{na['categoria']}</b> — N/A", styles["Label"]))
        story.append(Paragraph(na["justificativa"], styles["Body"]))
        story.append(Spacer(1, 0.2 * cm))
    story.append(PageBreak())

    # ---- Tabela de achados ----
    story.append(Paragraph("Tabela de achados detalhados", styles["H1"]))
    table_data = [[Paragraph("Sev.", styles["TableHeaderCell"]),
                   Paragraph("Arquivo:linha", styles["TableHeaderCell"]),
                   Paragraph("Descrição", styles["TableHeaderCell"])]]
    for f in sorted(ALL_FINDINGS, key=lambda x: SEVERITY_ORDER.index(x["severidade"])):
        table_data.append([
            severity_chip(f["severidade"]),
            Paragraph(f"{f['arquivo'].split('/')[-1]}<br/>{f['linhas']}", styles["TableCell"]),
            Paragraph(f"<b>[{f['id']}] {f['titulo']}</b>", styles["TableCell"]),
        ])
    findings_table = Table(table_data, colWidths=[2.9 * cm, 4.2 * cm, 8.6 * cm], repeatRows=1)
    findings_table.setStyle(TableStyle([
        ("VALIGN", (0, 0), (-1, -1), "MIDDLE"),
        ("BACKGROUND", (0, 0), (-1, 0), colors.HexColor("#111827")),
        ("TEXTCOLOR", (0, 0), (-1, 0), colors.white),
        ("GRID", (0, 0), (-1, -1), 0.4, colors.HexColor("#E5E7EB")),
        ("ROWBACKGROUNDS", (0, 1), (-1, -1), [colors.white, colors.HexColor("#F9FAFB")]),
        ("LEFTPADDING", (0, 0), (-1, -1), 6), ("RIGHTPADDING", (0, 0), (-1, -1), 6),
        ("TOPPADDING", (0, 0), (-1, -1), 5), ("BOTTOMPADDING", (0, 0), (-1, -1), 5),
    ]))
    story.append(findings_table)
    story.append(PageBreak())

    # ---- Detalhamento por achado ----
    story.append(Paragraph("Detalhamento arquivo por arquivo, linha por linha", styles["H1"]))
    for f in ALL_FINDINGS:
        block = []
        block.append(Paragraph(f"[{f['id']}] {f['titulo']}", styles["H2"]))
        block.append(severity_chip(f["severidade"]))
        block.append(Spacer(1, 0.15 * cm))
        block.append(Paragraph(f"<b>Arquivo:</b> {f['arquivo']}  <b>Linhas:</b> {f['linhas']}", styles["Body"]))
        block.append(Spacer(1, 0.1 * cm))
        block.append(Paragraph(f["trecho"].replace("\n", "<br/>").replace(" ", "&nbsp;"), styles["CodeBlock"]))
        block.append(Spacer(1, 0.15 * cm))
        block.append(Paragraph(f"<b>Descrição:</b> {f['descricao']}", styles["Body"]))
        block.append(Spacer(1, 0.1 * cm))
        block.append(Paragraph(f"<b>Por que é explorável:</b> {f['explorabilidade']}", styles["Body"]))
        block.append(Spacer(1, 0.1 * cm))
        block.append(Paragraph(f"<b>Impacto:</b> {f['impacto']}", styles["Body"]))
        if f.get("evidencia_extra"):
            block.append(Spacer(1, 0.1 * cm))
            block.append(Paragraph(f"<b>Evidência de verificação dinâmica:</b> {f['evidencia_extra']}", styles["Body"]))
        block.append(Spacer(1, 0.1 * cm))
        block.append(Paragraph(f"<b>Correção sugerida:</b> {f['correcao']}", styles["Body"]))
        block.append(Spacer(1, 0.35 * cm))
        block.append(HRFlowable(width="100%", color=colors.HexColor("#E5E7EB"), thickness=0.6))
        story.append(KeepTogether(block[:5]))
        story.extend(block[5:])
        story.append(Spacer(1, 0.25 * cm))
    story.append(PageBreak())

    # ---- Recomendações priorizadas ----
    story.append(Paragraph("Recomendações priorizadas", styles["H1"]))
    priorities = [
        ("P1 — Imediato", [
            "Corrigir a precedência lógica em LoanDAO.findLoansByUser() (linhas 74-76), "
            "adicionando parênteses à condição OR (F1).",
            "Remover a senha fixa 'admin/123' da migração V005 e forçar definição/troca de "
            "senha no primeiro acesso administrativo (F2).",
            "Implementar hashing de senha (BCrypt/Argon2id) em AuthDAO e UserDAO, migrando "
            "os registros existentes (F3).",
        ]),
        ("P2 — Curto prazo", [
            "Adicionar teste automatizado cobrindo isolamento de dados entre usuários, para "
            "evitar regressão da falha de precedência lógica (F1).",
            "Definir política mínima de senha (tamanho e complexidade) na captura de entrada "
            "(F4).",
        ]),
        ("P3 — Melhoria contínua", [
            "Substituir e.printStackTrace() por logging estruturado sem exposição de "
            "detalhes internos, especialmente antes de qualquer evolução para modo "
            "servidor/API (F5).",
            "Revisar periodicamente o evento agendado do MySQL (V004) quanto à consistência "
            "com a lógica da aplicação.",
        ]),
    ]
    for title, items in priorities:
        story.append(Paragraph(title, styles["H2"]))
        story.append(ListFlowable(
            [ListItem(Paragraph(it, styles["Body"]), leftIndent=8) for it in items],
            bulletType="bullet", start="•", leftIndent=14))
        story.append(Spacer(1, 0.2 * cm))
    story.append(PageBreak())

    # ---- Comunicação ao mantenedor ----
    story.append(Paragraph("Comunicação ao proprietário/mantenedor", styles["H1"]))
    story.append(Paragraph(f"<b>Mantenedor:</b> {MAINTAINER_COMM['mantenedor']}", styles["Body"]))
    story.append(Spacer(1, 0.1 * cm))
    story.append(Paragraph(f"<b>Canal:</b> {MAINTAINER_COMM['canal']}", styles["Body"]))
    story.append(Spacer(1, 0.1 * cm))
    story.append(Paragraph(f"<b>Data:</b> {MAINTAINER_COMM['data']}", styles["Body"]))
    story.append(Spacer(1, 0.1 * cm))
    if MAINTAINER_COMM["evidencias"]:
        story.append(Paragraph("<b>Evidências (links das Issues abertas):</b>", styles["Body"]))
        story.append(ListFlowable(
            [ListItem(Paragraph(f'{label}: <link href="{url}"><u>{url}</u></link>', styles["Body"]), leftIndent=8)
             for url, label in MAINTAINER_COMM["evidencias"]],
            bulletType="bullet", start="•", leftIndent=14))
    else:
        story.append(Paragraph(
            "<b>Evidências:</b> Issues a serem abertas no repositório GitHub logo após a "
            "emissão deste relatório; esta seção será atualizada com os links reais na "
            "versão final entregue.", styles["Body"]))
    story.append(PageBreak())

    # ---- Issues para o GitHub ----
    story.append(Paragraph("Issues para o GitHub", styles["H1"]))
    story.append(Paragraph(
        "Texto completo, em Markdown, pronto para copiar e colar na criação de Issues no "
        "GitHub. Achados triviais/relacionados do mesmo tema foram agrupados em uma única "
        "issue para evitar spam.", styles["Body"]))
    story.append(Spacer(1, 0.2 * cm))

    for issue in ISSUES:
        n = issue["numero"]
        md_lines = []
        md_lines.append(f"--- ISSUE {n} ---")
        md_lines.append(f"Título: {issue['titulo']}")
        md_lines.append(f"Labels: {issue['labels']}")
        md_lines.append("")
        md_lines.append("## Descrição do problema")
        md_lines.append(issue["descricao"])
        md_lines.append("")
        md_lines.append("## Evidência")
        md_lines.append(issue["evidencia"])
        md_lines.append("")
        md_lines.append("## Impacto")
        md_lines.append(issue["impacto"])
        md_lines.append("")
        md_lines.append("## Sugestão de correção")
        md_lines.append(issue["correcao"])
        md_lines.append("")
        md_lines.append("## Critérios de aceite")
        for c in issue["criterios"]:
            md_lines.append(f"- [ ] {c}")
        md_lines.append("")
        md_lines.append(f"--- FIM ISSUE {n} ---")

        full_md = "\n".join(md_lines)
        escaped = (full_md.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                   .replace("\n", "<br/>").replace(" ", "&nbsp;"))
        story.append(Paragraph(escaped, styles["IssueBody"]))
        story.append(Spacer(1, 0.35 * cm))

    doc.build(story, onFirstPage=header_footer, onLaterPages=header_footer)


if __name__ == "__main__":
    out = os.path.join(os.path.dirname(os.path.abspath(__file__)), "relatorio-auditoria-seguranca.pdf")
    build_pdf(out)
    print(f"PDF gerado em: {out}")

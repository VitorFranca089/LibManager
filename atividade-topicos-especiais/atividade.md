Objetivo
Use o arquivo prompt_seguranca.md para auditar um SaaS de sua autoria/responsabilidade ou um projeto open source autorizado. Identifique pelo menos uma vulnerabilidade CONFIRMADA no código real e comunique o proprietário ou mantenedor sobre a melhoria necessária.

A finalidade é educativa e responsável: ajudar a corrigir problemas e melhorar a segurança do SaaS/projeto auditado. Não explore, prejudique usuários nem obtenha vantagem.

O QUE FAZER
1. Escolha um SaaS próprio ou projeto open source cujo código esteja disponível e cujo uso para análise seja permitido.
2. Confirme o escopo e a autorização. Não teste sistemas de terceiros sem permissão explícita.
3. Use integralmente o prompt_seguranca.md, disponível no mural da turma.
4. Audite as cinco categorias do prompt: isolamento de usuário/tenant; permissões apenas no navegador; IDOR; chaves e credenciais expostas; inputs sem tratamento/XSS.
5. Encontre pelo menos uma vulnerabilidade confirmada. Informe arquivo, linha exata, trecho, por que é explorável, impacto e severidade. Não invente achados nem trate suspeitas como confirmação.
6. Gere o relatório em PDF conforme o prompt, incluindo a seção de issues para GitHub.
7. Comunique o achado ao proprietário/mantenedor por canal responsável, explicando o problema e sugerindo a melhoria. Evite publicar detalhes exploráveis antes da orientação do responsável.

ENTREGA
Envie UM ÚNICO ARQUIVO PDF GERADO PELO PROMPT. Ele deve conter: projeto e escopo autorizado; stack e metodologia; achados arquivo por arquivo e linha por linha; pelo menos uma vulnerabilidade confirmada; evidências, impacto, severidade e recomendações; pontos fortes e controles verificados; issues sugeridas; e registro da comunicação feita ao proprietário/mantenedor, com data, canal e link/evidência sem dados sensíveis.

Nome sugerido: relatorio-auditoria-seguranca-NomeSobrenome.pdf

SEGURANÇA E ÉTICA
- Teste somente dentro do escopo autorizado.
- Não acesse, copie, divulgue ou altere dados reais de outras pessoas.
- Não tente obter contas, tokens, senhas ou informações privadas.
- Não faça negação de serviço, spam, phishing, engenharia social ou alterações destrutivas.
- Use dados fictícios e contas de teste.
- Ao confirmar a falha, pare no ponto suficiente para demonstrá-la e comunique o responsável.
- Remova tokens, senhas, dados pessoais e outras informações sensíveis do PDF.
- Respeite o canal e as orientações do proprietário/mantenedor.

PRAZO E MODALIDADE
Atividade em grupo. Entrega até 21/09/2026, às 23h59, horário de Brasília. Não serão aceitas entregas após o prazo.

CRITÉRIOS DE AVALIAÇÃO
Aplicação do prompt; evidência técnica verificável; vulnerabilidade real sem especulação; clareza do impacto e da melhoria; cobertura das categorias aplicáveis e dos controles corretos; qualidade do PDF; responsabilidade e qualidade da comunicação ao proprietário/mantenedor.

O objetivo não é derrubar ou expor o sistema. É produzir uma análise fundamentada que ajude o responsável a corrigir uma vulnerabilidade e melhorar a segurança do projeto.
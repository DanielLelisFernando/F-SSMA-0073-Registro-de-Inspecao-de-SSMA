# Projeto — Aplicativo de Registro de Inspeções de SSMA

> **Estado atual (26/11/2026):** o aplicativo já implementa os fluxos descritos neste inventário. A versão 2.2.9 foi validada no run #93 (testes unitários + APK debug). A 2.2.10 acrescenta proteção contra sobrescrita silenciosa de rascunho ao editar pelo histórico e aguarda CI. Testes em aparelho e validação funcional integral continuam pendentes. As seções históricas 8–10 registram a baseline documental inicial.


## Documento de referência

- **Formulário:** F-SSMA-0073 — Registro de Inspeção de SSMA
- **Revisão:** Rev.00
- **Data indicada:** 31/08/2022
- **Extensão:** 2 páginas
- **Regra de conformidade:** o formulário é a fonte oficial para campos, nomenclaturas e classificações. Não presumir obrigatoriedade, validações ou dados que não estejam expressos no documento.

## 1. Estrutura documental observada

### Cabeçalho (páginas 1 e 2)
- Identidade visual/logotipo SCL / Salum Construções.
- Código F-SSMA-0073.
- Título: REGISTRO DE INSPEÇÃO DE SSMA.
- Revisão e data: Rev.00 - 31/08/2022.
- Numeração: Página 1 de 2 e Página 2 de 2.

### Identificação da inspeção (página 1)
- Responsável inspeção:
- Nº Inspeção:
- Data:
- Local:

### Participantes (página 1)
Tabela com os cabeçalhos **Nome dos participantes** e **Assinatura**. Há linhas para múltiplos participantes; o formulário não estabelece limite máximo digital.

### Classificação da Severidade (página 1)

| Classificação | Descrição constante do formulário |
|---|---|
| LEVE | Condição de risco/desvio que não gera transtornos à saúde e ocasiona acidentes que demandam somente primeiros socorros ou impacto ambiental não significativo. |
| MODERADA | Condição de risco/desvio que gera transtornos à saúde e ocasiona acidentes sem afastamento (com restrição médica) ou impacto ambiental que afeta ecossistemas comuns. |
| GRAVE | Condição de risco/desvio que gera transtornos à saúde e ocasiona acidentes com afastamento ou até uma fatalidade; impacto ambiental que afeta ecossistemas com espécies raras e afeta a comunidade. |
| CRÍTICO | Evento não planejado com liberação de energia que poderia resultar em mais de uma fatalidade ou lesão incapacitante permanente, dada ligeira mudança no tempo, posição ou atuação dos controles. |

As descrições devem acompanhar a seleção para orientar o usuário. O formulário não apresenta fórmula de cálculo ou critérios complementares de desempate.

### Registro de condições e ações (páginas 1 e 2)
Tabela repetitiva com as colunas originais:
1. Nº
2. CONDIÇÃO DE RISCO/ NÃO CONFORME
3. SEVERIDADE
4. AÇÃO PARA ADEQUAÇÃO/CORREÇÃO
5. PRAZO DE EXECUÇÃO
6. RESPONSÁVEL

A tabela inicia na página 1 e continua na página 2. O app deverá permitir registros adicionais sem limitar a quantidade aos espaços impressos.

### Validação/assinatura final (página 2)
Linha de assinatura com a legenda **Assinatura / Matrícula do Responsável pela Inspeção**.

## 2. Inventário campo a campo e componente futuro

### Extensão aprovada pelo usuário — foto opcional do desvio

Além do formulário impresso, o usuário autorizou uma extensão digital para cada registro de condição/desvio: **foto opcional**, selecionável da galeria ou capturada pela câmera do aparelho. O campo textual **CONDIÇÃO DE RISCO/ NÃO CONFORME** (R02) permanece **obrigatório** na implementação, conforme decisão explícita do usuário. Essa regra de obrigatoriedade é uma regra de produto aprovada, não uma exigência que conste do formulário Rev.00. A fotografia é opcional e não deve impedir a conclusão quando ausente.

A interface deverá permitir **no máximo uma foto por desvio**: (a) selecionar uma imagem existente usando o seletor de fotos/galeria do Android; ou (b) capturar uma foto pela câmera durante o preenchimento. Permitir visualizar e substituir/remover a foto antes de concluir. O armazenamento deverá ser local e offline. A foto, quando presente, deverá ser incorporada também ao PDF exportado/gerado, junto ao desvio correspondente, com layout adaptativo e sem truncar o texto obrigatório. Essa extensão deve ser identificada no app e no PDF como recurso digital adicional, sem alterar ou alegar que o formulário corporativo original Rev.00 contém campo de fotografia.


| ID | Seção/página | Nome original | Finalidade | Tipo/componente futuro | Obrigatoriedade | Opções/regras observadas | Repetição |
|---|---|---|---|---|---|---|---|
| H01 | Cabeçalho, 1–2 | F-SSMA-0073 | Identificar o formulário | Texto fixo | N/A | Código impresso | Não |
| H02 | Cabeçalho, 1–2 | REGISTRO DE INSPEÇÃO DE SSMA | Título documental | Texto fixo | N/A | Preservar nomenclatura | Não |
| H03 | Cabeçalho, 1–2 | Rev.00 - 31/08/2022 | Identificar revisão | Texto fixo/metadado | N/A | Preservar revisão efetivamente usada | Não |
| H04 | Cabeçalho, 1–2 | Página 1 de 2 / Página 2 de 2 | Paginação documental | Texto fixo no PDF | N/A | PDF pode ter páginas adicionais, mantendo cabeçalho | Por página |
| I01 | Identificação, 1 | Responsável inspeção: | Identificar responsável pela inspeção | Texto de uma linha | Não indicada | Sem formato ou validação especificados | Não |
| I02 | Identificação, 1 | Nº Inspeção: | Identificar a inspeção | Texto curto/número | Não indicada | Máscara e regra de geração não definidas | Não |
| I03 | Identificação, 1 | Data: | Registrar data da inspeção | Seletor de data | Não indicada | Formato não especificado | Não |
| I04 | Identificação, 1 | Local: | Registrar local | Texto de uma linha | Não indicada | Não há lista predefinida | Não |
| P01 | Participantes, 1 | Nome dos participantes | Identificar participante | Texto por participante | Não indicada | Uma linha por participante | Sim |
| P02 | Participantes, 1 | Assinatura | Registrar assinatura do participante | Captura de assinatura | Não indicada | Vinculada ao participante | Sim, com P01 |
| S01 | Severidade, 1 | LEVE | Classificar severidade da condição | Opção de seleção única | Não indicada | Descrição oficial LEVE | Por condição |
| S02 | Severidade, 1 | MODERADA | Classificar severidade da condição | Opção de seleção única | Não indicada | Descrição oficial MODERADA | Por condição |
| S03 | Severidade, 1 | GRAVE | Classificar severidade da condição | Opção de seleção única | Não indicada | Descrição oficial GRAVE | Por condição |
| S04 | Severidade, 1 | CRÍTICO | Classificar severidade da condição | Opção de seleção única | Não indicada | Descrição oficial CRÍTICO | Por condição |
| R01 | Condições/ações, 1–2 | Nº | Numerar ocorrência | Índice numérico automático | Não indicada | Sequência dos registros; regra de renumeração não definida | Sim |
| R02 | Condições/ações, 1–2 | CONDIÇÃO DE RISCO/ NÃO CONFORME | Descrever condição identificada | Campo descritivo multilinha | **Obrigatória no aplicativo (decisão aprovada)** | Sem categoria ou limite definido; não é marcada como obrigatória no formulário impresso | Sim |
| R07 | Extensão digital aprovada | Foto do desvio (campo digital adicional) | Evidência visual opcional da condição | Seleção de uma imagem da galeria ou captura pela câmera; preview e remover/substituir | **Opcional; máximo de 1 foto por desvio** | Imagem local/offline; não consta do formulário Rev.00; incluir no PDF exportado quando presente | Sim, uma foto por ocorrência
| R03 | Condições/ações, 1–2 | SEVERIDADE | Classificar a condição | Seleção única | Não indicada | LEVE, MODERADA, GRAVE ou CRÍTICO | Sim |
| R04 | Condições/ações, 1–2 | AÇÃO PARA ADEQUAÇÃO/CORREÇÃO | Registrar ação | Campo descritivo multilinha | Não indicada | Sem opções predefinidas | Sim |
| R05 | Condições/ações, 1–2 | PRAZO DE EXECUÇÃO | Registrar prazo para a ação | Data ou texto de prazo | Não indicada | O formulário não define formato; manter flexível até confirmação | Sim |
| R06 | Condições/ações, 1–2 | RESPONSÁVEL | Identificar responsável pela ação | Texto de uma linha | Não indicada | Sem lista predefinida | Sim |
| A01 | Validação, 2 | Assinatura / Matrícula do Responsável pela Inspeção | Registrar identificação/assinatura final | Captura de assinatura + texto de matrícula | Não indicada | Rótulo combinado; o documento não apresenta campo separado nem formato de matrícula | Não |

**Obrigatoriedade:** nenhum campo de entrada está marcado com asterisco, legenda de obrigatório ou instrução equivalente. Assim, o formulário não sustenta marcar campos como obrigatórios ou bloquear conclusão por obrigatoriedade presumida. A política deve ser confirmada pelo proprietário do processo.

## 3. Mapeamento para modelo de dados

Modelo lógico inicial, sem introduzir regras ausentes:

### Inspection
- `id`: identificador técnico único local.
- `form_code`: F-SSMA-0073.
- `form_revision`: Rev.00.
- `form_effective_date`: 31/08/2022.
- `inspection_responsible`: I01.
- `inspection_number`: I02.
- `inspection_date`: I03.
- `location`: I04.
- `responsible_signature`: assinatura A01 (arquivo/representação local).
- `responsible_registration`: matrícula A01.
- `created_at`, `completed_at`, `app_version`, `status`: metadados técnicos de rastreabilidade, não campos impressos.

### InspectionParticipant
- `id`
- `inspection_id` (FK)
- `name` (P01)
- `signature` (P02; representação local)

Relação: uma inspeção para vários participantes.

### InspectionFinding
- `id`
- `inspection_id` (FK)
- `sequence_number` (R01)
- `risk_condition_nonconformity` (R02; **obrigatório no app**, regra aprovada pelo usuário, não impressa no formulário original)\n- `photo_uri` ou referência ao arquivo local de foto (R07; opcional, associada à ocorrência; origem galeria ou câmera; guardar arquivo local e referência no banco)
- `severity` (R03; valores: LEVE, MODERADA, GRAVE, CRÍTICO)
- `corrective_action` (R04)
- `execution_deadline` (R05; manter representação compatível com a entrada definida)
- `responsible` (R06)

Relação: uma inspeção para várias condições/ações.

### FormDefinition
Catálogo versionado contendo código/revisão, nomenclatura, campos, descrições de severidade e configuração do PDF. Cada inspeção concluída deve preservar a referência exata da definição utilizada.

## 4. Mapeamento para PDF final

| Região do PDF | Dados de origem |
|---|---|
| Cabeçalho de cada página | Logotipo corporativo, F-SSMA-0073, título, Rev.00 - 31/08/2022 e paginação |
| Identificação na primeira página | I01–I04 |
| Tabela de participantes | P01–P02, uma linha por participante |
| Classificação da Severidade | LEVE, MODERADA, GRAVE, CRÍTICO e respectivas descrições oficiais |
| Tabela de condições e ações | Uma linha por ocorrência, R01–R06; R02 é obrigatório no app; R07 é foto opcional e, se presente, pode ser incorporada como evidência adicional ao PDF |
| Continuação em páginas adicionais | Novas linhas com o mesmo cabeçalho e colunas originais |
| Fecho da última página | A01 — Assinatura / Matrícula do Responsável pela Inspeção |

O PDF não poderá truncar texto nem omitir ocorrências. A quantidade de linhas por página poderá variar por legibilidade, preservando ordem, nomenclatura e conteúdo integral.

## 5. Componentes de interface derivados

1. **Identificação:** Responsável inspeção, Nº Inspeção, Data e Local.
2. **Participantes:** lista dinâmica de Nome dos participantes e Assinatura.
3. **Classificação de severidade:** seletor entre as quatro classes oficiais, com descrições visíveis.
4. **Condições e ações:** lista dinâmica com Nº, CONDIÇÃO DE RISCO/ NÃO CONFORME (**descrição obrigatória no app**), SEVERIDADE, AÇÃO PARA ADEQUAÇÃO/CORREÇÃO, PRAZO DE EXECUÇÃO e RESPONSÁVEL; foto opcional da ocorrência via galeria ou câmera.
5. **Assinatura final:** captura de assinatura e matrícula do responsável, mantendo a legenda original.

## 6. Pendências de confirmação — não presumir

- Quais campos são obrigatórios e quais bloqueiam a conclusão.
- Se a assinatura de cada participante é exigida e qual forma é aceita.
- Como separar matrícula e assinatura do responsável final.
- Se PRAZO DE EXECUÇÃO deve ser data estrita ou texto livre.
- Se Nº Inspeção é digitado ou gerado por procedimento corporativo.
- Se existe catálogo corporativo de locais ou responsáveis (não aparece no formulário).
- Se deve haver exatamente uma severidade por condição (o leiaute sugere uma célula, mas não há regra textual de seleção).
- Se existe aprovação adicional (não há outro campo de aprovação visível).

Até confirmação, manter entrada flexível e não implementar validações de negócio inventadas.

## 7. Escopo técnico previsto

- Captura/seleção de fotografia opcional por ocorrência: galeria e câmera Android; armazenar imagem local e incluir no PDF quando disponível. Usar mecanismos de acesso a mídia/câmera apropriados ao Android, solicitando permissões apenas quando necessárias.
- Descrição R02 obrigatória no app, regra de produto aprovada, com validação objetiva para não permitir concluir uma ocorrência sem texto.

- Aplicativo Android com persistência local SQLite.
- Operação offline para criar, editar rascunhos, consultar e gerar PDF.
- PDF local com páginas adicionais para registros repetitivos.
- Exportação de PDFs individuais e ZIP.
- Compartilhamento pelo mecanismo nativo Android.
- Versionamento Git e migrações locais não destrutivas.
- Preservação da revisão do formulário e da versão do app em cada registro.

## 8. Estado da versão 1.0.2 — limite de foto confirmado\n\n- **Versão do projeto:** 1.0.2 — atualização documental.\n- **Decisão:** máximo de uma foto por desvio, escolhida da galeria ou capturada pela câmera; foto opcional; incluir no PDF exportado.\n- **Implementação e testes:** ainda pendentes.\n\n## 9. Estado da versão 1.0.1 — alteração de escopo aprovada

- **Versão do projeto:** 1.0.1 — atualização documental de escopo.
- **Alteração:** descrição do desvio R02 passa a ser obrigatória no app; fotografia por ocorrência é opcional, via galeria ou câmera, e pode ser incluída no PDF.
- **Nota:** 1.0.1 não é build Android; é atualização documental de requisito.

## 10. Estado da baseline 1.0.0

- **Versão do projeto:** 1.0.0 — baseline documental.
- **Análise do formulário:** inventário e mapeamentos documentados.
- **Implementação Android:** não iniciada.
- **Build/APK:** não disponível.
- **Testes funcionais:** não executados.
- **Pendências:** seção 6.

As atualizações documentais 1.0.1 e 1.0.2 e a baseline 1.0.0 não representam APK funcional nem versão de produção.

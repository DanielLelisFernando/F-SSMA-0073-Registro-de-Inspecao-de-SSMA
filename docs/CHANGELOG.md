## 2.2.11 — validação CI pendente\n- PDF reorganizado em tabela paginada com as seis colunas principais de condições e ações, repetindo o cabeçalho da tabela nas páginas de continuação.\n- Evidências fotográficas permanecem vinculadas ao desvio e são apresentadas após a linha correspondente.\n- versionCode 16 / versionName 2.2.11.\n- Build acionado; validação visual em aparelho ainda pendente.\n\n# Histórico de alterações

## 2.2.10 — validada (build #95)
- Protege rascunho existente ao abrir edição de inspeção pelo histórico: oferece retomar ou descartar e editar, evitando sobrescrita silenciosa.
- versionCode 15 / versionName 2.2.10.
- Testes unitários e APK debug concluídos com sucesso no run #95; artifact publicado.


## 2.2.9 — validada (build #93)
- Corrige newline literal no arquivo Gradle Kotlin DSL que causava erro de sintaxe na build #92.
- Corrige retorno antecipado na função local `openStream()` para corpo de função explícito, resolvendo erro Kotlin #91.
- Mantém decodificação de imagens em resolução limitada e leitura de orientação EXIF.
- versionCode 14 / versionName 2.2.9.
- Testes unitários e APK debug concluídos com sucesso no run #93; artifact publicado.

## 2.2.8 — falhou CI (#91/#92)
- Implementação de decodificação limitada e orientação EXIF; correção de build incorporada na 2.2.9.

## 2.2.7 — validada (build #90)
- Validação antes do salvamento: responsável, data válida, local e descrição de todos os desvios.
- Testes unitários para campos obrigatórios, data bissexta e rejeição de data inválida.
- versionCode 12 / versionName 2.2.7.
- Testes unitários e APK debug concluídos com sucesso no run #90; artifact publicado.

## 2.2.6 — validada (build #86)
- Utilitário de conversão/validação de datas extraído para código Kotlin independente.
- Parsing estrito do formato dd/MM/yyyy, incluindo rejeição de datas inválidas e texto residual.
- Testes unitários para parsing, formatação UTC e ordenação por data.
- CI passa a executar testDebugUnitTest antes de assembleDebug.
- versionCode 11 / versionName 2.2.6.
- CI executa testDebugUnitTest e assembleDebug; run #86, commit 87a1651c, sucesso; artifact publicado.

## 2.2.5 — validada (build #80)
- Filtros do histórico por data inicial/final com calendário, validação de datas e intervalo.
- ZIP exporta todas as inspeções selecionadas independentemente do filtro visual.
- Pastas no ZIP incluem identificador local para evitar colisões.
- Build debug #80, commit ff744c6c, sucesso; artifact publicado.

## 2.2.4 — validada (build #76)
- Compartilhamento de arquivo único usa ACTION_SEND e MIME específico (PDF/ZIP).
- Calendário de data preseleciona a data registrada ao editar.
- Build debug #76, commit 82f0e6bb, sucesso; artifact publicado.

## 2.2.3 — validada (build #74)
- Corrige conversão UTC nos seletores de inspeção e prazo.
- Build debug #74, commit f3b1aa2f, sucesso; artifact publicado.

## 2.2.2 — validada (build #71)
- Galeria: copia imagens para armazenamento interno privado.
- Câmera: captura usando URI/FileProvider para arquivo local.
- PDF: quebra de texto, cabeçalho/rodapé, severidades, participantes e assinaturas.
- ZIP inclui PDFs e evidências fotográficas/assinaturas.
- Histórico com pesquisa por número, local ou responsável e filtros por data.
- Autosave offline, retomada pela home e confirmação antes de descartar rascunho.
- Build debug #71, commit 1476b4cf, sucesso; artifact publicado.

## 2.2.1 — baseline
- Build debug CI run #58, commit 271b9861, sucesso; APK publicado.
- Participantes, assinaturas, matrícula, logo Salum, calendário, filtros e PDF básico integrados.

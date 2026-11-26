# Histórico de alterações

## 2.2.6 — em validação
- Utilitário de conversão/validação de datas extraído para código Kotlin independente.
- Parsing estrito do formato dd/MM/yyyy, incluindo rejeição de datas inválidas e texto residual.
- Testes unitários para parsing, formatação UTC e ordenação por data.
- CI passa a executar testDebugUnitTest antes de assembleDebug.
- versionCode 11 / versionName 2.2.6.
- Build CI pendente.

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

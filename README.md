# Inspeções SSMA Android

Aplicativo Android nativo (Kotlin/Jetpack Compose) para registro digital offline de inspeções da Salum Construções, conforme F-SSMA-0073 Rev.00.

**Versão 2.2.23 — validação do arquivo PNG da assinatura; CI pendente.**

A etiqueta de versão da tela inicial foi sincronizada com o versionName do Gradle.

## Funcionalidades
- Identificação da inspeção, numeração local, data e local.
- Participantes e assinaturas; matrícula e assinatura do responsável.
- Desvios repetíveis com severidade, ação, prazo, responsável e foto opcional.
- Câmera e galeria com armazenamento local.
- SQLite, autosave, consulta, pesquisa, edição e filtros por data.
- PDF com cabeçalho, paginação, tabela de desvios, severidades, fotos e assinaturas.
- Compartilhamento e exportação ZIP de inspeções selecionadas.
- Funcionamento offline.

## Validação
Build debug 2.2.20 (run #124) e 2.2.21 (run #125) com testes unitários e APK aprovados; artifacts publicados. A 2.2.22 adiciona validação de traços visíveis. A 2.2.23 valida o PNG produzido e descarta arquivos incompletos; CI pendente. As falhas históricas de Kotlin/Gradle foram corrigidas nas versões posteriores. Testes funcionais em aparelho permanecem pendentes.

Testes em aparelho/emulador e validação funcional integral permanecem pendentes.

## Documentação
- [Checklist mestre](docs/CHECKLIST-MESTRE-REVISAO.md)
- [Análise do formulário](docs/ANALISE-FORMULARIO-F-SSMA-0073.md)
- [Changelog](docs/CHANGELOG.md)

## Versão 2.2.14 — build #102 falhou
- ZIP selecionado passa a incluir `Manifest_Evidencias.txt`, identificando evidências copiadas e ausentes por inspeção.
- Exportação informa a contagem de evidências incluídas/ausentes.
- Testes unitários para coleta de referências e manifesto.
- CI pendente.

## Versão 2.2.13
- Compartilhamento via `ClipData` para reforçar concessão de leitura de PDFs e ZIPs a apps destinatários.
- Filtra arquivos inexistentes antes de iniciar o compartilhamento.
- Build #100 falhou por junção de funções Kotlin; correção validada no run #101.

## Versão 2.2.12
- Cabeçalho tabular ampliado e reserva de espaço para assinaturas/evidências no PDF.
- Builds #98/#99 aprovadas; validação visual em aparelho permanece pendente.


## Versão 2.2.16 — CI em validação
- Exportação ZIP copia evidências para staging temporário antes de inserir no arquivo, evitando entradas parciais causadas por falhas de leitura.
- Manifesto registra bytes e SHA-256 para evidências incluídas; evidências ausentes são sinalizadas.
- Falha de gravação do ZIP cancela a exportação e exibe erro, sem compartilhar arquivo incompleto.
- versionCode 21 / versionName 2.2.16.

## Versão 2.2.15 — validada (builds #103/#104)
- Corrige o retorno da função local de cópia `addUri` no ZIP e o tipo `ExportEvidenceStatus`.
- Atualiza `versionCode` 20 / `versionName` 2.2.15.
- Runs #103/#104, testes unitários e APK debug aprovados.


## Versão 2.2.17 — CI em validação
- Corrige a separação de expressões Kotlin após o botão de exportação ZIP, antes do diálogo de retomada/descartar rascunho.
- versionCode 22 / versionName 2.2.17.
- CI aguardando execução.


## Versão 2.2.18 — CI em validação
- Fotos de galeria preservam a extensão identificada pelo MIME do Android; exportação ZIP usa a extensão armazenada, com fallback JPG.
- Testes unitários para WebP e extensão desconhecida.
- versionCode 23 / versionName 2.2.18.


## Versão 2.2.19 — CI em validação
- Normaliza extensões de imagens importadas e rejeita URI inacessível ou arquivo vazio; remove staging parcial.
- Testes unitários para extensão MIME e fallback JPG.
- versionCode 24 / versionName 2.2.19.


## Versão 2.2.20 — CI em validação
- Captura da câmera valida assinatura JPEG antes de associar a foto, removendo arquivos vazios ou inválidos.
- Testes unitários adicionados para assinatura JPEG e rejeição de arquivos inválidos.
- versionCode 25 / versionName 2.2.20; resultado CI pendente.


## Versão 2.2.20 — validada (build #124)
- Validação da captura da câmera por assinatura JPEG.
- Testes unitários e APK debug aprovados; artifact publicado.

## Versão 2.2.21 — validada (build #125)
- Removidos logs temporários de rastreamento da seleção de data e prazo.
- Testes unitários e APK debug aprovados; artifact publicado.
- versionCode 26 / versionName 2.2.21.


## Versão 2.2.22 — CI em validação
- O botão de salvar assinatura só habilita quando existe traço visível; ponto único ou movimento nulo não geram assinatura em branco.
- Testes unitários para traços vazios, ponto único, estacionários e válidos.
- versionCode 27 / versionName 2.2.22.

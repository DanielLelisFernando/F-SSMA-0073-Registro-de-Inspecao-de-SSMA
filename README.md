# Inspeções SSMA Android

Aplicativo Android nativo (Kotlin/Jetpack Compose) para registro digital offline de inspeções da Salum Construções, conforme F-SSMA-0073 Rev.00.

**Versão 2.2.13 — compilação em validação.**

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
Build debug 2.2.11 (runs #96/#97) e 2.2.12 (runs #98/#99) com testes unitários e APK concluídos com sucesso; artifacts publicados. A 2.2.8 falhou nas builds #91/#92 por erros de Kotlin e sintaxe Gradle, corrigidos e validados na 2.2.9. A 2.2.10 protege rascunhos ao editar registros; testes unitários e APK debug concluídos com sucesso no run #95 (commit `7a07e771`).

Testes em aparelho/emulador e validação funcional integral permanecem pendentes.

## Documentação
- [Checklist mestre](docs/CHECKLIST-MESTRE-REVISAO.md)
- [Análise do formulário](docs/ANALISE-FORMULARIO-F-SSMA-0073.md)
- [Changelog](docs/CHANGELOG.md)

## Versão 2.2.13
- Compartilhamento via `ClipData` para reforçar concessão de leitura de PDFs e ZIPs a apps destinatários.
- Filtra arquivos inexistentes antes de iniciar o compartilhamento.
- Build #100 identificou erro de sintaxe Kotlin na separação entre funções; corrigido e CI reacionado.

## Versão 2.2.12
- Cabeçalho tabular ampliado e reserva de espaço para assinaturas/evidências no PDF.
- Builds #98/#99 aprovadas; validação visual em aparelho permanece pendente.

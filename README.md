# Inspeções SSMA Android

Aplicativo Android nativo (Kotlin/Jetpack Compose) para registro digital offline de inspeções da Salum Construções, conforme F-SSMA-0073 Rev.00.

**Versão de desenvolvimento: 2.2.10 (em validação).**

## Funcionalidades
- Identificação da inspeção, numeração local, data e local.
- Participantes e assinaturas; matrícula e assinatura do responsável.
- Desvios repetíveis com severidade, ação, prazo, responsável e foto opcional.
- Câmera e galeria com armazenamento local.
- SQLite, autosave, consulta, pesquisa, edição e filtros por data.
- PDF com cabeçalho, paginação, severidades, fotos e assinaturas.
- Compartilhamento e exportação ZIP de inspeções selecionadas.
- Funcionamento offline.

## Validação
Build debug 2.2.7 (run #90) e 2.2.9 (run #93) com testes unitários concluídos com sucesso; artifacts publicados. A 2.2.8 falhou nas builds #91/#92 por erros de Kotlin e sintaxe Gradle, corrigidos e validados na 2.2.9. A 2.2.10 protege rascunhos ao editar registros e aguarda CI.

Testes em aparelho/emulador e validação funcional integral permanecem pendentes.

## Documentação
- [Checklist mestre](docs/CHECKLIST-MESTRE-REVISAO.md)
- [Análise do formulário](docs/ANALISE-FORMULARIO-F-SSMA-0073.md)
- [Changelog](docs/CHANGELOG.md)

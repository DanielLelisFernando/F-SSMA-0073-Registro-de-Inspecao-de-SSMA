# SCL Inspeções — F-SSMA-0073

Aplicativo Android nativo (Kotlin/Jetpack Compose) para registro digital offline de inspeções de SSMA da Salum Construções.

**Versão de desenvolvimento do código: 2.2.4.**

## Funcionalidades
- Identificação da inspeção e numeração automática.
- Participantes com assinaturas individuais.
- Assinatura e matrícula do responsável.
- Registro repetível de desvios com severidade, ação, prazo, responsável e foto opcional.
- Fotos copiadas para armazenamento interno privado.
- Consulta, edição, filtro por datas e pesquisa por número/local/responsável.
- PDF com cabeçalho/paginação, quebra de texto, classificação de severidade, fotos e assinaturas.
- Exportação ZIP contendo PDFs e evidências.
- Compartilhamento via FileProvider.
- Recuperação automática de rascunho local durante o preenchimento (retomada pela home).\n- Operação offline.

## Build
O workflow GitHub Actions executa `gradle assembleDebug` e publica o APK debug como artifact.

**Baseline anterior validada:** versão 2.2.1, run #58, commit `271b9861c0ea00521b9a1c5682452d1b375a65ff` — build e publicação do APK com sucesso.

A versão 2.2.2 foi validada no run #71 (commit `1476b4cf`) e a 2.2.3 no run #74 (commit `f3b1aa2f`). A versão 2.2.4 está em validação de build. Testes em aparelho/emulador não foram executados por CI e permanecem pendentes.

## Documentação
- [Checklist mestre](docs/CHECKLIST-MESTRE-REVISAO.md)
- [Análise do formulário](docs/ANALISE-FORMULARIO-F-SSMA-0073.md)
- [Histórico de versões](docs/CHANGELOG.md)

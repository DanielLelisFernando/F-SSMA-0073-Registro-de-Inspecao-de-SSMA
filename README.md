# F-SSMA-0073 — Aplicativo de Registro de Inspeções de SSMA

**Versão do projeto: 1.0.0 — Baseline documental**

Aplicativo Android offline planejado para digitalizar o formulário corporativo **F-SSMA-0073 — Registro de Inspeção de SSMA** (Rev.00 - 31/08/2022).

## Situação atual

A versão **1.0.0** formaliza a análise do documento-base, o inventário campo a campo, o modelo lógico inicial e o mapeamento do PDF. A implementação do aplicativo ainda não começou; não há APK ou build funcional nesta versão.

## Análise oficial do formulário

Consulte [docs/ANALISE-FORMULARIO-F-SSMA-0073.md](docs/ANALISE-FORMULARIO-F-SSMA-0073.md).

## Regra de desenvolvimento

O formulário corporativo é a fonte oficial. Não adicionar campos, classificações, obrigatoriedades ou validações sem evidência documental ou aprovação explícita do proprietário do processo. Ambiguidades devem permanecer registradas como pendências.

## Próximas etapas

1. Validar as pendências do inventário com o responsável pelo processo SSMA.
2. Definir arquitetura Android e esquema SQLite versionado.
3. Implementar telas e fluxo de inspeção.
4. Implementar PDF fiel ao formulário, exportação ZIP e compartilhamento.
5. Executar testes funcionais, offline, de integridade e compatibilidade.
6. Gerar APK e registrar a release após validação.

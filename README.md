# Inspeções SSMA Android

**Versão 1.0.2 — protótipo em desenvolvimento.**

O aplicativo Android tem como objetivo digitalizar o formulário corporativo F-SSMA-0073 Rev.00, com operação offline.

## Funcionalidades atualmente presentes no código
- Identificação básica (responsável, número, data em texto e local).
- Desvios repetíveis com descrição, severidade, ação, prazo e responsável.
- Descrição do desvio obrigatória (regra de produto).
- Foto opcional por desvio, escolha pela galeria ou captura simples pela câmera, visualização e remoção.
- Geração básica de PDF e tentativa de compartilhamento.
- Banco SQLite simplificado.

## Limitações conhecidas
Participantes e suas assinaturas, assinatura/matrícula final do responsável, histórico/pesquisa/edição, armazenamento durável de fotos, persistência estruturada de cada desvio, ZIP, FileProvider para compartilhamento, layout corporativo fiel, paginação robusta, logo e validação completa em dispositivo ainda não estão concluídos ou validados.

## Diretriz visual
Paleta temática configurada no código: azul institucional `#252D87`, ciano `#20A9D2`, azul claro `#EAF6FC`, branco e fundo `#F4F8FC`. A aplicação ainda não implementa integralmente o cabeçalho/cartões corporativos nem o layout final.

## Controle de revisão
Consulte [Checklist Mestre](docs/CHECKLIST-MESTRE-REVISAO.md) para o plano de conformidade e lacunas, baseado no formulário corporativo original. O checklist não representa, por si só, funcionalidade implementada ou testada.

## Build
GitHub Actions é usado para compilar o APK debug e disponibilizar o artifact quando a execução conclui com sucesso. A compilação não substitui testes funcionais no aparelho.


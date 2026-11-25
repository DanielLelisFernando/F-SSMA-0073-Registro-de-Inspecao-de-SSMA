# Inspeções SSMA Android

**Versão de desenvolvimento 1.0.2 (MVP em implementação).**

APK debug é construído por GitHub Actions. MVP inclui identificação da inspeção, desvios repetitivos, descrição obrigatória, severidade, ação, prazo, responsável, foto opcional (máximo de uma por desvio) via galeria/câmera, geração de PDF e compartilhamento.

> Limitações do protótipo: fluxo e validação funcional ainda precisam de testes; assinatura, participantes, pesquisa/histórico, exportação ZIP, PDF com layout fiel ao formulário corporativo e teste em dispositivos ainda não implementados/validados.

Workflow: GitHub Actions → Android APK → artifact `ssma-inspecoes-debug-apk`.


## Diretriz visual (implementada no MVP)

- Identidade visual inspirada na marca SCL/Salum Construções.
- Paleta: azul institucional `#252D87`, ciano `#20A9D2`, azul claro `#EAF6FC`, branco e fundo claro `#F4F8FC`.
- Cabeçalho institucional destacado, cartões arredondados, campos agrupados, botões de ação evidentes e fluxo mobile.
- Esta atualização é visual; a validação final dos campos e do PDF com o formulário oficial F-SSMA-0073 permanece necessária.

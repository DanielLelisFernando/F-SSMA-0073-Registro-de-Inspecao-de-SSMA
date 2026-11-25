# Inspeções SSMA Android

**Versão de desenvolvimento: 2.1.0** (`versionCode 5`).

Aplicativo Android nativo em Kotlin/Jetpack Compose para digitalização do formulário corporativo F-SSMA-0073 Rev.00. A versão 2.1.0 está em desenvolvimento na branch `2.1.0`.

## Escopo solicitado para 2.1.0

- [ ] Ícone do APK: usar `icone apk Inspeções.png` como ícone adaptativo e legado.
- [ ] Inserir o logo Salum no cabeçalho da tela inicial.
- [ ] Redesenhar a tela inicial com padrão visual institucional.
- [ ] Interceptar o botão Voltar do Android para navegar pela pilha interna de telas.
- [x] Ajuste de janela com `adjustResize` para evitar sobreposição em cenários com teclado.
- [ ] Corrigir insetos de barras de status/navegação e validar em diferentes versões de Android.
- [ ] Aumentar área de toque e dimensão do botão Voltar.
- [ ] Substituir entrada textual de data por seletor de data localizado.
- [ ] Gerar número sequencial de inspeção a partir da sequência persistida no banco.
- [ ] Implementar exclusão individual de desvio.
- [ ] Nomear PDFs como `Inspeção_SCL_data_local.pdf` (com sanitização dos caracteres inválidos).
- [ ] Inserir logo Salum no cabeçalho de todas as páginas do PDF.

## Estado técnico e ressalvas

Apenas a versão do aplicativo/build foi atualizada e o manifesto recebeu `adjustResize`. Os demais itens ainda não devem ser considerados concluídos. A aplicação precisa de implementação e validação funcional em emulador/aparelho Android.

O projeto atual tem uma `MainActivity.kt` centralizada, armazenamento SQLite e geração de PDF com `PdfDocument`. O formulário corporativo original possui duas páginas: identificação (responsável, número, data, local e participantes), classificação de severidade (LEVE, MODERADA, GRAVE e CRÍTICO), tabela de desvios com ação, prazo e responsável, e assinatura/matrícula final do responsável. Essas áreas devem continuar representadas no PDF final.

## Build e validação

O workflow GitHub Actions em `.github/workflows/android.yml` executa `gradle assembleDebug` e publica o APK debug como artifact. **Build verificada:** GitHub Actions executou `Build APK` com sucesso na branch `2.1.0` (run #28, commit `360a6bfa`). Artifact `ssma-inspecoes-debug-apk` foi publicado e está disponível até 25/12/2026. [Abrir execução](https://github.com/DanielLelisFernando/F-SSMA-0073-Registro-de-Inspecao-de-SSMA/actions/runs/45684522510).

## Histórico de desenvolvimento

- `2.1.0`: branch aberta para as adequações solicitadas.

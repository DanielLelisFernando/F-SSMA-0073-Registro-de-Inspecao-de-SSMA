## 2.2.20 — validada (build #124)
- Captura pela câmera valida assinatura JPEG antes de vincular o arquivo ao desvio; rejeita arquivos vazios, ausentes ou de formato não JPEG e remove o arquivo rejeitado.
- Testes unitários para JPEG, arquivo vazio, assinatura desconhecida e arquivo inexistente.
- versionCode 25 / versionName 2.2.20.

## 2.2.19 — CI em validação
- Corrige o texto da versão exibida na tela inicial, alinhando APP_VERSION ao versionName do Gradle.
- Normaliza a extensão de mídia da galeria e evita registrar foto quando a URI não pode ser aberta ou resulta em arquivo vazio; o arquivo parcial é removido.
- Testes unitários para normalização da extensão MIME e fallback JPG.
- versionCode 24 / versionName 2.2.19.

## 2.2.18 — CI em validação
- Fotos selecionadas da galeria passam a ser armazenadas com extensão correspondente ao MIME informado pelo Android, com fallback JPG.
- ZIP preserva a extensão da foto no nome do arquivo exportado; testes para WebP e fallback.
- versionCode 23 / versionName 2.2.18.

## 2.2.17 — CI em validação
- Corrige separação de expressões Kotlin no fechamento do botão de exportação ZIP e no diálogo de rascunho do histórico.
- versionCode 22 / versionName 2.2.17.
- CI aguardando execução.

## 2.2.16 — CI em validação
- Evidências do ZIP são copiadas para staging temporário antes da inclusão, reduzindo risco de entradas parciais por falha de leitura.
- Manifesto registra tamanho em bytes e SHA-256 para evidências incluídas, além de identificar evidências ausentes.
- Falha na escrita do ZIP aborta a exportação e evita compartilhamento de arquivo incompleto.
- versionCode 21 / versionName 2.2.16.

## 2.2.14 — build #102 falhou
- ZIP de inspeções selecionadas inclui manifesto com status de cada foto/assinatura e contagem de ausentes.
- Feedback ao usuário após exportação informa quantas evidências foram copiadas.
- Testes unitários adicionados para coleta de evidências e manifesto.
- Build #102 falhou por erros de retorno Kotlin e nome de tipo; correção registrada na 2.2.15.

## 2.2.13 — validada (build #101)
- Compartilhamento individual e múltiplo passa a incluir ClipData além de EXTRA_STREAM, reforçando a concessão de URI read permission aos aplicativos destinatários.
- Filtra arquivos inexistentes antes de montar a intent.
- versionCode 18 / versionName 2.2.13.
- Build #100 falhou por ausência de quebra de linha entre `shareFiles` e `pdf`; corrigido no commit `abaf91b7`.
- Run #101, commit `abaf91b7`, executou `testDebugUnitTest` e `assembleDebug` com sucesso; artifact publicado.
- Teste real de compartilhamento em aparelho permanece pendente.


## 2.2.15 — validada (builds #103/#104)
- Corrige o retorno da função local `addUri` no ZIP e o tipo `ExportEvidenceStatus` usado na coleta.
- Atualiza `versionCode` 20 e `versionName` 2.2.15.
- Runs #103/#104 executaram testes unitários e assembleDebug com sucesso; artifact publicado.


## Versão 2.2.21
- Remove instrumentação temporária de rastreamento de seleção de datas e prazos do código de produção.
- Mantém o parsing/formatação de datas UTC e os testes existentes.
- versionCode 26 / versionName 2.2.21.
- Baseline 2.2.20 validada no run #124: testes unitários e APK debug aprovados.


## 2.2.21 — validada (build #125)
- Removidos logs temporários de rastreamento de datas e prazos.
- Testes unitários e APK debug aprovados; artifact publicado.

## 2.2.22 — CI em validação
- Captura de assinatura habilita salvar apenas quando existe movimento de traço visível; rejeita traço de ponto único ou movimento nulo.
- Testes unitários para traços vazios, ponto único, movimento nulo e movimento visível.
- versionCode 27 / versionName 2.2.22.

# Checklist mestre — F-SSMA-0073 Rev.00

Atualizado para a versão **2.2.23**. A 2.2.20 foi aprovada no run #124, a 2.2.21 no run #125 e a 2.2.22 nos runs #126–#129. A 2.2.23 valida a gravação PNG da assinatura; CI pendente. CI não substitui validação funcional em aparelho.

## Implementado no código
- [x] Identificação: responsável, número automático, data por calendário e local.
- [x] Participantes repetíveis e captura de assinatura individual.
- [x] Matrícula e assinatura do responsável.
- [x] Desvios repetíveis, descrição obrigatória, severidade, ação, prazo e responsável.
- [x] Descrição das quatro severidades no formulário e PDF.
- [x] Câmera com captura para arquivo privado local.
- [x] Galeria com cópia para armazenamento privado.
- [x] Visualização, substituição e remoção de foto por desvio.
- [x] SQLite, consulta, edição, pesquisa e filtro por data.
- [x] PDF com cabeçalho, paginação, rodapé, texto longo, assinaturas e evidências.
- [x] Compartilhamento individual por FileProvider.
- [x] ZIP com PDF e evidências.
- [x] Logo Salum na home e no PDF.
- [x] Autosave de rascunho e retomada pela home.
- [x] Filtro de histórico por calendário e exportação de registros selecionados (2.2.5).

## Validação técnica
- [x] Build debug baseline 2.2.1: run #58, sucesso.
- [x] Build debug 2.2.2: run #71, sucesso.
- [x] Build debug 2.2.3: run #74, sucesso.
- [x] Build debug 2.2.4: run #76, sucesso.
- [x] Build debug 2.2.5: run #80, commit ff744c6c, sucesso; artifact publicado.
- [x] Testes unitários e build debug 2.2.6: run #86, commit 87a1651c, sucesso; artifact publicado.
- [x] Build debug 2.2.11: runs #96 e #97, sucesso; artifacts publicados.
- [x] Build debug 2.2.12: runs #98 e #99, sucesso; artifacts publicados.
- [ ] Testes em aparelho/emulador.
- [ ] Teste offline integral.
- [ ] Criar inspeção com múltiplos desvios e fotos, fechar/reabrir.
- [ ] Conferir edição e substituição/remoção de fotos.
- [ ] Conferir PDF em leitor externo.
- [ ] Conferir ZIP e compartilhamento.
- [ ] Testar atualização sobre banco existente.

- [x] 2.2.16: staging temporário para evidências ZIP, manifesto com bytes e SHA-256; erro de escrita aborta exportação e não compartilha arquivo incompleto. CI pendente.

## Pendências conhecidas
- [x] Corrige newline literal no `app/build.gradle.kts` que impedia script Gradle de compilar (runs #92).
- [x] Corrige `openStream()` com retorno dentro de corpo de expressão e habilita EXIF via dependência válida (run #91/#92).
- [x] Recuperação de rascunho offline.
- [x] Conversão UTC nos DatePicker (build #74).
- [x] Preseleção da data ao editar (build #76).
- [x] Filtro de histórico por datas e exportação selecionada (build #80).
- [x] Testes automatizados de parsing, UTC, ordenação e datas inválidas (build #86).
- [x] Validação de campos obrigatórios de responsável, data, local e descrição dos desvios (2.2.7, run #90).
- [x] Validação de decodificação limitada e orientação EXIF de imagens (2.2.9, run #93, sucesso; artifact publicado).
- [x] Proteção para edição de inspeção existente quando há rascunho ativo (2.2.10, run #95, sucesso; artifact publicado).
- [ ] Testes de lifecycle, permissões e tamanhos de tela.
- [x] PDF 2.2.11: tabela paginada para condições, severidade, ação, prazo e responsável, com cabeçalho de tabela repetido ao continuar entre páginas (runs #96/#97 aprovados).
- [x] PDF 2.2.12: altura do cabeçalho tabular ajustada e espaço reservado para manter rótulos de assinaturas/evidências junto às imagens; builds #98/#99 aprovadas.
- [x] Compartilhamento 2.2.13 via ClipData: run #101, commit `abaf91b7`, testes unitários e APK debug aprovados.
- [x] ZIP 2.2.14: manifesto de evidências e contagem de ausentes implementados no código.
- [x] Correção 2.2.15: retorno explícito da função local `addUri` e referência correta ao `ExportEvidenceStatus`.
- [x] CI 2.2.15: runs #103/#104, testes unitários e APK debug aprovados.
- [ ] Validar transferência de PDFs/ZIPs via ClipData em aparelho.
- [ ] Validar layout do PDF contra o formulário original em leitor externo/aparelho.
- [ ] Confirmar captura de câmera, assinaturas e exportações no aparelho.

## Regras de escopo
- Operação offline.
- Descrição do desvio obrigatória por decisão de produto.
- Foto opcional, máximo de uma por desvio.
- Formulário de referência: F-SSMA-0073 Rev.00, 31/08/2022.
- Não marcar validação em aparelho apenas porque CI compila.

- [x] 2.2.17: corrigida separação de expressões Kotlin no histórico após exportação ZIP; versionCode 22 / versionName 2.2.17.
- [ ] CI 2.2.17: testes unitários e APK debug.

- [x] 2.2.18: preserva a extensão MIME das fotos da galeria e o nome no ZIP; testes unitários adicionados. CI pendente.


- [x] 2.2.19: normalização da extensão MIME, importação de galeria rejeita URI sem stream/arquivo vazio e remove arquivo parcial; testes unitários adicionados. CI pendente.


- [x] 2.2.18: preserva extensão MIME de fotos importadas e exportadas; testes adicionados. CI não confirmado.


- [x] 2.2.20: captura da câmera valida assinatura de imagem e rejeita arquivos vazios/desconhecidos antes de associar a evidência; testes unitários adicionados. CI pendente.


- [x] Diagnóstico do run #105 (commit `c44435b2`, versão 2.2.16): `compileDebugKotlin` falhou em `MainActivity.kt:361` com `Syntax error: Unexpected tokens`; a correção de separação das expressões foi commitada na 2.2.17, mas seu CI não foi confirmado neste acesso.
- [x] 2.2.20: validação da captura da câmera pela assinatura JPEG; testes unitários adicionados e verificação local isolada da função passou. CI do aplicativo completo permanece pendente de confirmação.


- [x] 2.2.20: testes unitários e APK debug aprovados no run #124 (commit `a4f9b6e4`); artifact publicado.
- [x] 2.2.21: removidos logs temporários `SSMA_DATE_TRACE` do fluxo de seleção de data e prazo; testes de datas existentes preservados. CI aprovado no run #125.


- [x] 2.2.22: diálogo de assinatura exige traço visível (mais de um ponto e deslocamento); testes unitários adicionados.
- [x] CI 2.2.22: testes unitários e APK debug aprovados nos runs #126/#127/#128/#129; artifacts publicados #128/#129.


- [x] 2.2.23: assinatura é gravada em staging, valida cabeçalho PNG e tamanho mínimo antes de ser associada; falhas removem arquivos incompletos. Testes para cabeçalho válido, inválido e arquivo truncado. CI pendente.

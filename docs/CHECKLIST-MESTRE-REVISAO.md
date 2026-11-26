# Checklist mestre — F-SSMA-0073 Rev.00

Atualizado para a versão **2.2.17**. A 2.2.16 falhou no CI por separação de expressões Kotlin no fechamento do botão de exportação; a correção 2.2.17 foi commitada e aguarda CI. CI não substitui validação funcional em aparelho. A versão 2.2.12 foi validada nos runs #98 e #99. A 2.2.8 teve falhas de CI #91/#92 por erro na função local de abertura de streams e newline literal no Gradle; esses pontos foram corrigidos na 2.2.9. CI não substitui validação funcional em aparelho.

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

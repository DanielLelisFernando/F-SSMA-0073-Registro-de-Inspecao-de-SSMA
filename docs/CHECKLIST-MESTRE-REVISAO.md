# Checklist mestre — F-SSMA-0073 Rev.00

Atualizado para a versão **2.2.6**. A versão 2.2.5 foi validada no build #80. A 2.2.6 adiciona testes automatizados de datas e está aguardando CI. Build e testes de unidade não substituem validação funcional em aparelho.

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
- [ ] Testes unitários e build debug 2.2.6.
- [ ] Testes em aparelho/emulador.
- [ ] Teste offline integral.
- [ ] Criar inspeção com múltiplos desvios e fotos, fechar/reabrir.
- [ ] Conferir edição e substituição/remoção de fotos.
- [ ] Conferir PDF em leitor externo.
- [ ] Conferir ZIP e compartilhamento.
- [ ] Testar atualização sobre banco existente.

## Pendências conhecidas
- [x] Recuperação de rascunho offline.
- [x] Conversão UTC nos DatePicker (build #74).
- [x] Preseleção da data ao editar (build #76).
- [x] Filtro de histórico por datas e exportação selecionada (build #80).
- [ ] Testes de lifecycle, permissões e tamanhos de tela.
- [ ] Validar layout do PDF contra o formulário original; conteúdo representado, mas tabela não reproduz fielmente o desenho.
- [ ] Confirmar captura de câmera, assinaturas e exportações no aparelho.

## Regras de escopo
- Operação offline.
- Descrição do desvio obrigatória por decisão de produto.
- Foto opcional, máximo de uma por desvio.
- Formulário de referência: F-SSMA-0073 Rev.00, 31/08/2022.
- Não marcar validação em aparelho apenas porque CI compila.

# Checklist mestre — F-SSMA-0073 Rev.00

Atualizado para a versão **2.2.5**. A versão 2.2.4 foi validada no build #76; as melhorias de filtro de datas e exportação selecionada da 2.2.5 estão em validação. Os itens marcados como implementados indicam presença no código; os testes de uso real continuam separados.

## Implementado no código
- [x] Identificação: responsável, número automático, data por calendário e local.
- [x] Lista repetível de participantes e captura de assinatura individual.
- [x] Matrícula e assinatura do responsável pela inspeção.
- [x] Desvios repetíveis, descrição obrigatória, severidade, ação, prazo e responsável.
- [x] Descrição oficial das quatro severidades exibida no formulário e no PDF.
- [x] Câmera com captura para arquivo privado local.
- [x] Galeria com cópia da foto para armazenamento privado do app.
- [x] Visualizar, substituir e remover foto por desvio.
- [x] SQLite para inspeções e conteúdo estruturado em JSON para desvios/participantes.
- [x] Consulta, edição, filtro por data e pesquisa por número/local/responsável.
- [x] PDF com cabeçalho institucional repetido, paginação, rodapé e quebra de texto.
- [x] PDF inclui identificação, participantes/assinaturas, legenda de severidades, desvios, fotos, matrícula e assinatura final.
- [x] Compartilhamento individual por FileProvider.
- [x] ZIP com PDF e evidências (fotos e assinaturas).
- [x] Logo Salum na home e no PDF.
- [x] Ícone launcher vetorial com lupa.

## Validação técnica
- [x] Build debug CI da baseline 2.2.1: run #58, commit `271b9861`, sucesso; APK publicado.
- [x] Build debug versão 2.2.2: run #71, commit `1476b4cf`, sucesso; artifact publicado.
- [x] Build debug versão 2.2.3: run #74, commit `f3b1aa2f`, sucesso; APK artifact publicado.
- [x] Build debug versão 2.2.4 (run #76, commit `82f0e6bb`, sucesso; artifact publicado).\n- [ ] Build debug versão 2.2.5 (filtro de datas por calendário, validação de intervalo e exportação de selecionadas independentemente do filtro visual).
- [ ] Testes em aparelho/emulador.
- [ ] Teste offline integral.
- [ ] Criar inspeção com múltiplos desvios e fotos, fechar e reabrir.
- [ ] Conferir edição, substituição/removação de fotos, filtros e número automático.
- [ ] Conferir PDF em leitor externo (paginação, textos e imagens).
- [ ] Conferir ZIP (PDF, fotos e assinaturas) e compartilhamento.
- [ ] Testar atualização sobre banco já existente.

## Pendências conhecidas
- [x] Recuperação de rascunho offline: autosave durante o preenchimento, retomada na home e confirmação antes de descartar ao iniciar novo registro.
- [x] Conversão UTC dos DatePicker: validada no build #74.
- [x] Calendário de data da inspeção preseleciona o valor atual do registro (2.2.4; run #75/#76, sucesso).\n- [ ] Filtro de histórico por calendário (início/fim), validação de datas e exportação ZIP de todas as inspeções selecionadas (2.2.5).
- [ ] Testes de lifecycle/permissões e em diferentes tamanhos de tela.
- [ ] Validar layout do PDF visualmente contra o formulário original (o conteúdo está representado, mas a tabela ainda não é uma reprodução gráfica exata).
- [ ] Confirmar no aparelho a captura de câmera, assinaturas e exportações.

## Regras
- Operação offline.
- Descrição do desvio obrigatória por decisão de produto.
- Foto opcional, máximo de uma por desvio.
- Formulário de referência: F-SSMA-0073 Rev.00, 31/08/2022.
- Não marcar validação de dispositivo como concluída apenas porque o build compila.

# Revisão geral e checklist mestre — F-SSMA-0073 Rev.00

Auditoria baseada no formulário corporativo original e na versão de código atualmente existente no repositório. Este checklist acompanha a implementação; caixas marcadas indicam implementação e não apenas planejamento.

## Fase 1 — Conformidade documental
- [ ] Responsável pela inspeção
- [x] Número da inspeção
- [x] Data (seletor de calendário implementado; validação em aparelho pendente)
- [x] Local
- [ ] Participantes: lista repetível de nomes
- [ ] Assinatura de cada participante
- [x] Condições/desvios repetíveis
- [x] Descrição da condição (regra de produto: obrigatória)
- [x] Severidade: LEVE, MODERADA, GRAVE, CRÍTICO
- [x] Ação de adequação/correção
- [x] Prazo
- [x] Responsável pelo item
- [ ] Assinatura e matrícula do responsável pela inspeção

## Fase 2 — Interface e fluxo
- [ ] Cabeçalho institucional com logo
- [ ] Agrupar identificação em cartão
- [ ] Home com iniciar inspeção, histórico e exportações
- [ ] UX consistente, acessibilidade e feedback
- [ ] Validar visualmente em diferentes tamanhos de tela

## Fase 3 — Robustez e persistência
- [ ] Persistência estruturada dos itens (não concatenar descrições)
- [ ] Salvar fotos em armazenamento persistente privado
- [ ] Garantir edição/substituição/removação de foto
- [ ] Recuperação de rascunho
- [ ] Compartilhamento seguro de arquivos via FileProvider/content URI (remover Uri.fromFile)
- [ ] Verificar permissões, lifecycle e falhas

## Fase 4 — Gestão
- [ ] Histórico de inspeções
- [ ] Pesquisa e filtros
- [ ] Abrir/editar inspeção salva
- [ ] Exportar PDF individual
- [ ] Exportar pacote ZIP com PDF e evidências
- [ ] Compartilhamento e exportação testados

## Fase 5 — PDF e validação
- [ ] Layout fiel ao formulário F-SSMA-0073 Rev.00
- [ ] Logo e identificação corporativa
- [ ] Participantes e assinaturas
- [ ] Fotos com legenda/associação ao desvio
- [ ] Paginação, cabeçalho repetido e rodapé
- [ ] Não truncar texto nem evidências
- [ ] Testes em aparelho/emulador
- [ ] Teste integral offline
- [ ] Teste de persistência, exportação e compartilhamento

## Extensão digital aprovada
Foto opcional por desvio, capturada pela câmera ou galeria, armazenada localmente e incorporada ao PDF. Essa extensão não consta no formulário Rev.00 original e deve ser identificada como recurso digital adicional.

## Regra de rastreabilidade
Não considerar item concluído por existir apenas no checklist, documentação, ou por compilar. Marcar como implementado somente após código e validação; registrar limitações e testes.


## Versão 2.1.0 — requisitos de implementação e critérios de aceite\n\n**Atualização de verificação (25/11/2026):** o build da correção Kotlin passou no GitHub Actions (run #34). Foram confirmadas no código alterações para calendário, navegação de retorno, exclusão de desvio e geração automática de número. A aprovação de compilação não equivale à validação em aparelho; os itens de interface, PDF, insets, logos, sequência após exclusão e comportamento de exportação permanecem em aberto.

- [ ] Ícone do aplicativo configurado em mipmap (launcher legado e adaptive icon) usando o PNG aprovado.
- [ ] Logo Salum visível no cabeçalho da página inicial e no cabeçalho de todas as páginas do PDF.
- [ ] Tela inicial redesenhada e validada em diferentes densidades/tamanhos.
- [x] Botão Voltar do Android navega para home a partir das telas internas (comportamento de saída na raiz ainda a validar).
- [ ] Window insets aplicados às barras de status e navegação; conteúdo não pode ficar encoberto.
- [ ] Botões de navegação com área de toque mínima apropriada e botão Voltar visualmente maior.
- [x] Campo DATA usa seletor de calendário localizado em pt-BR (persistência/formato canônico a validar).
- [x] Geração automática de número baseada no maior ID existente implementada; requisito de sequência transacional e não reutilização após exclusões ainda pendente.
- [x] Ação de excluir desvio implementada (confirmação e integridade a validar).
- [x] Sanitização de componentes de nome de arquivo implementada; padrão final do nome e validação de exportação pendentes.
- [ ] PDF segue as duas páginas do formulário, incluindo participantes/assinaturas, severidade, desvios, prazos e assinatura/matrícula final.
- [ ] Testes funcionais em aparelho/emulador: criar duas inspeções, fechar/reabrir app, conferir sequência; editar e excluir desvio; validar botão Voltar e insets; exportar/abrir PDF e checar nome, logo e paginação.\n- [x] Build debug verificada via GitHub Actions: etapa `Build APK` e upload de artifact concluídos com sucesso (run #34, commit `5f8a5e3`).

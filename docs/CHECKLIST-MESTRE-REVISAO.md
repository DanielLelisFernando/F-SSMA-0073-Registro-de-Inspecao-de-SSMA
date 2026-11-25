# Revisão geral e checklist mestre — F-SSMA-0073 Rev.00

Auditoria baseada no formulário corporativo original e na versão de código atualmente existente no repositório. Este checklist acompanha a implementação; caixas marcadas indicam implementação e não apenas planejamento.

## Fase 1 — Conformidade documental
- [ ] Responsável pela inspeção
- [x] Número da inspeção
- [x] Data (atualmente entrada textual; evoluir para seletor)
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

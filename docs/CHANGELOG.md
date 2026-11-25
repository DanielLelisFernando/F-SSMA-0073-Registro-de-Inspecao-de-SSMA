# Histórico de alterações

## 2.2.4 — em validação
- Compartilhamento de arquivo único usa `ACTION_SEND` e MIME específico (PDF/ZIP), evitando encaminhar ZIP como lista de múltiplos anexos.
- Ao editar uma inspeção, o calendário de data abre com a data já registrada selecionada.
- versionCode 9 / versionName 2.2.4.
- Build CI pendente.

## 2.2.3 — validada (build #74)
- Corrige conversão entre `selectedDateMillis` (UTC) e datas exibidas em `dd/MM/yyyy` nos seletores de inspeção e prazo.
- Build debug CI #74, commit `f3b1aa2f`, concluído com sucesso; APK artifact publicado.

## 2.2.3 — em validação
- Corrige conversão entre `selectedDateMillis` (UTC) e datas exibidas em `dd/MM/yyyy` nos seletores de inspeção e prazo, evitando deslocamento de dia em fusos negativos.
- Incrementa versionCode para 8 e versionName para 2.2.3.
- Build CI pendente.


## 2.2.2 — em validação
- Galeria: copia imagens para armazenamento interno privado, reduzindo dependência de permissões temporárias de URI.
- Câmera: captura usando URI/FileProvider para arquivo local, substituindo captura de preview de baixa resolução.
- PDF: quebra de texto, cabeçalho e rodapé em páginas adicionais, descrição oficial das severidades, participantes e assinaturas.
- Evidências fotográficas identificadas como extensão digital adicional e vinculadas ao desvio no PDF.
- ZIP: inclui PDFs e fotos/assinaturas associadas.
- Histórico: busca por número, local ou responsável, mantendo filtro por datas.\n- Rascunho: autosave offline e retomada pela home; ao iniciar novo registro com rascunho, confirmação para descartá-lo.
- Versão de aplicativo atualizada para 2.2.2.
- Build e teste em aparelho desta versão: pendentes.

## 2.2.1 — baseline
- Build debug CI run #58, commit `271b9861c0ea00521b9a1c5682452d1b375a65ff`, concluído com sucesso.
- Participantes, assinaturas, matrícula, logo Salum, calendário, filtros de data e PDF básico integrados.
- APK artifact publicado no GitHub Actions.

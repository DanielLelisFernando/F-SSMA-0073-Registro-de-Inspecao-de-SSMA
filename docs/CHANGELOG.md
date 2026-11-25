# Histórico de alterações

## 2.2.5 — em validação
- Filtros do histórico por data inicial e final com calendário, entradas read-only e ações de limpeza.
- Validação de datas e prevenção de intervalo inicial posterior ao final.
- Exportação ZIP agora considera todas as inspeções selecionadas, mesmo se a pesquisa/filtro atual mudar.
- Caminhos de inspeção dentro do ZIP recebem ID local para evitar colisões de nomes.
- versionCode 10 / versionName 2.2.5.
- Build CI pendente.

## 2.2.4 — validada (build #76)
- Compartilhamento de arquivo único usa `ACTION_SEND` e MIME específico (PDF/ZIP).
- Calendário de data preseleciona a data registrada ao editar.
- versionCode 9 / versionName 2.2.4.
- Build debug CI #76, commit `82f0e6bb`, sucesso; artifact publicado.

## 2.2.3 — validada (build #74)
- Corrige conversão UTC nos seletores de inspeção e prazo.
- versionCode 8 / versionName 2.2.3.
- Build debug CI #74, commit `f3b1aa2f`, sucesso; artifact publicado.

## 2.2.2 — validada (build #71)
- Galeria: copia imagens para armazenamento interno privado.
- Câmera: captura usando URI/FileProvider para arquivo local.
- PDF: quebra de texto, cabeçalho/rodapé, severidades, participantes e assinaturas.
- ZIP inclui PDFs e evidências fotográficas/assinaturas.
- Histórico com pesquisa por número, local ou responsável e filtros por data.
- Autosave offline, retomada pela home e confirmação antes de descartar rascunho.
- Build debug CI #71, commit `1476b4cf`, sucesso; artifact publicado.

## 2.2.1 — baseline
- Build debug CI run #58, commit `271b9861c0ea00521b9a1c5682452d1b375a65ff`, sucesso.
- Participantes, assinaturas, matrícula, logo Salum, calendário, filtros e PDF básico integrados.
- APK artifact publicado.

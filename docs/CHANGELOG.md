## 2.2.14 — CI pendente
- ZIP de inspeções selecionadas inclui manifesto com status de cada foto/assinatura e contagem de ausentes.
- Feedback ao usuário após exportação informa quantas evidências foram copiadas.
- Testes unitários adicionados para coleta de evidências e manifesto.
- Build e validação CI pendentes.

## 2.2.13 — validada (build #101)
- Compartilhamento individual e múltiplo passa a incluir ClipData além de EXTRA_STREAM, reforçando a concessão de URI read permission aos aplicativos destinatários.
- Filtra arquivos inexistentes antes de montar a intent.
- versionCode 18 / versionName 2.2.13.
- Build #100 falhou por ausência de quebra de linha entre `shareFiles` e `pdf`; corrigido no commit `abaf91b7`.
- Run #101, commit `abaf91b7`, executou `testDebugUnitTest` e `assembleDebug` com sucesso; artifact publicado.
- Teste real de compartilhamento em aparelho permanece pendente.

undefined
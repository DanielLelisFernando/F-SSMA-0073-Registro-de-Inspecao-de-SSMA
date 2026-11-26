## 2.2.13 — validada (build #101)
- Compartilhamento individual e múltiplo passa a incluir ClipData além de EXTRA_STREAM, reforçando a concessão de URI read permission aos aplicativos destinatários.
- Filtra arquivos inexistentes antes de montar a intent.
- versionCode 18 / versionName 2.2.13.
- Build #100 falhou por ausência de quebra de linha entre `shareFiles` e `pdf`; corrigido no commit `abaf91b7`.
- Run #101, commit `abaf91b7`, executou `testDebugUnitTest` e `assembleDebug` com sucesso; artifact publicado.
- Teste real de compartilhamento em aparelho permanece pendente.

undefined
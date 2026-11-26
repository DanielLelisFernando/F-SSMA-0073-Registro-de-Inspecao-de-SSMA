# Inspeções SSMA PWA iOS 1.0.0

Implementação inicial independente para PWA, isolada na branch ios-1.0.0.

## Executar
Hospede a pasta `ios/` em servidor HTTPS. No iPhone, abrir no Safari e usar Compartilhar > Adicionar à Tela de Início.

## Recursos
- Cadastro e edição local de inspeções, participantes, desvios, severidade, ação, prazo e responsável.
- Foto associada ao desvio (captura ou biblioteca, conforme dispositivo).
- Assinatura desenhada.
- IndexedDB para dados.
- Histórico, pesquisa e filtros por data.
- Relatório imprimível/PDF pelo diálogo do sistema.
- Exportação/restauração JSON.
- Service Worker e cache offline inicial.
- Solicitação de armazenamento persistente.

## Limitações e pendências de teste
- Teste real em iPhone/Safari/PWA instalado é obrigatório.
- Assinaturas de participantes ainda não implementadas nesta primeira entrega.
- PDF é gerado pelo fluxo de impressão do navegador, não por layout PDF dedicado.
- ZIP e manifesto de evidências/SHA-256 ainda não estão implementados.
- A exportação JSON preserva registros e evidências baseadas em data URL, mas arquivos grandes podem afetar memória.
- Offline, câmera, fotos, persistência, atualização e restauração precisam validação.
- A numeração sequencial é local e pode conflitar entre dispositivos.
- armazenamento não tem garantia absoluta contra limpeza pelo sistema.

Esta versão é uma entrega inicial de implementação, não uma versão aprovada para uso operacional.

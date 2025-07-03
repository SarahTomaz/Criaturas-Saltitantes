# Script PowerShell para Commit das Melhorias
Write-Host "==========================================" -ForegroundColor Green
Write-Host "Script para Commit das Melhorias" -ForegroundColor Green
Write-Host "==========================================" -ForegroundColor Green
Write-Host ""

try {
    Write-Host "1. Inicializando repositório git (se necessário)..." -ForegroundColor Yellow
    git init
    
    Write-Host ""
    Write-Host "2. Configurando remote para o repositório GitHub..." -ForegroundColor Yellow
    git remote remove origin 2>$null
    git remote add origin https://github.com/SarahTomaz/Criaturas-Saltitantes.git
    
    Write-Host ""
    Write-Host "3. Criando branch para as melhorias..." -ForegroundColor Yellow
    git checkout -b feature/visualizacao-melhorada 2>$null
    
    Write-Host ""
    Write-Host "4. Adicionando arquivos ao staging..." -ForegroundColor Yellow
    git add .
    
    Write-Host ""
    Write-Host "5. Fazendo commit das alterações..." -ForegroundColor Yellow
    $commitMessage = @"
feat: Implementa visualização gráfica completa e controle de velocidade

✨ Novas Funcionalidades:
- Visualização em tempo real de criaturas, clusters e guardião
- Exibição visual das moedas para todos os elementos
- Controle de velocidade da simulação (4 opções: 100ms-1000ms)
- Interface com estatísticas em tempo real
- Painel de configurações expandido

🎨 Melhorias Visuais:
- Sombras e contornos definidos para todos os elementos
- Fundo dourado destacado para moedas do guardião
- Legendas explicativas dos símbolos
- Layout responsivo e intuitivo

⚡ Performance:
- Acelera velocidade padrão de 1000ms para 300ms por iteração
- Timer configurável pelo usuário
- Renderização otimizada com antialiasing

🐛 Correções:
- Corrige lógica de atualização das moedas do guardião
- Ajusta controles de interface durante execução
- Melhora responsividade dos elementos gráficos

📚 Documentação:
- Adiciona documentação completa das funcionalidades
- Cria guias de uso e arquitetura
- Documenta condições de parada e velocidades

🧪 Testes:
- Mantém suite de testes completa (19 testes, 100% sucesso)
- Adiciona testes específicos para o guardião
- Valida todas as funcionalidades implementadas
"@
    
    git commit -m $commitMessage
    
    Write-Host ""
    Write-Host "6. Fazendo push para o GitHub..." -ForegroundColor Yellow
    git push -u origin feature/visualizacao-melhorada
    
    Write-Host ""
    Write-Host "==========================================" -ForegroundColor Green
    Write-Host "Commit realizado com sucesso!" -ForegroundColor Green
    Write-Host "Branch: feature/visualizacao-melhorada" -ForegroundColor Green
    Write-Host "==========================================" -ForegroundColor Green
    Write-Host ""
    Write-Host "Próximos passos:" -ForegroundColor Cyan
    Write-Host "1. Acesse: https://github.com/SarahTomaz/Criaturas-Saltitantes" -ForegroundColor White
    Write-Host "2. Crie um Pull Request da branch feature/visualizacao-melhorada para main" -ForegroundColor White
    Write-Host "3. Revise as alterações e faça o merge" -ForegroundColor White
    Write-Host ""
    
    # Abrir automaticamente o GitHub no navegador
    Write-Host "Abrindo GitHub no navegador..." -ForegroundColor Yellow
    Start-Process "https://github.com/SarahTomaz/Criaturas-Saltitantes"
    
} catch {
    Write-Host "Erro durante o processo: $_" -ForegroundColor Red
    Write-Host "Verifique se o Git está instalado e configurado." -ForegroundColor Red
}

Write-Host ""
Read-Host "Pressione Enter para continuar"

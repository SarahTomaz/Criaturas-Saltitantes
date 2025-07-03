@echo off
echo ==========================================
echo Script para Commit das Melhorias
echo ==========================================
echo.

echo 1. Inicializando repositorio git (se necessario)...
git init

echo.
echo 2. Configurando remote para o repositorio GitHub...
git remote add origin https://github.com/SarahTomaz/Criaturas-Saltitantes.git

echo.
echo 3. Criando branch para as melhorias...
git checkout -b feature/visualizacao-melhorada

echo.
echo 4. Adicionando arquivos ao staging...
git add .

echo.
echo 5. Fazendo commit das alteracoes...
git commit -m "feat: Implementa visualização gráfica completa e controle de velocidade

- Adiciona visualização em tempo real de criaturas, clusters e guardião
- Implementa exibição visual das moedas para todos os elementos
- Adiciona controle de velocidade da simulação (4 opções)
- Melhora interface com estatísticas em tempo real
- Acelera velocidade padrão de 1s para 300ms por iteração
- Adiciona sombras e melhorias visuais nos elementos
- Implementa painel de configurações expandido
- Corrige lógica de atualização das moedas do guardião
- Adiciona documentação completa das funcionalidades"

echo.
echo 6. Fazendo push para o GitHub...
git push -u origin feature/visualizacao-melhorada

echo.
echo ==========================================
echo Commit realizado com sucesso!
echo Branch: feature/visualizacao-melhorada
echo ==========================================
echo.
echo Proximos passos:
echo 1. Acesse: https://github.com/SarahTomaz/Criaturas-Saltitantes
echo 2. Crie um Pull Request da branch feature/visualizacao-melhorada para main
echo 3. Revise as alteracoes e faca o merge
echo.
pause

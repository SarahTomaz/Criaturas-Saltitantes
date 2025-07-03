@echo off
echo ==========================================
echo Script para Commit das Melhorias
echo ==========================================
echo.

echo 1. Inicializando repositorio git (se necessario)...
git init

echo.
echo 2. Configurando remote para o repositorio GitHub...
git remote remove origin 2>nul
git remote add origin https://github.com/SarahTomaz/Criaturas-Saltitantes.git

echo.
echo 3. Criando branch para as melhorias...
git checkout -b feature/visualizacao-melhorada 2>nul || git checkout feature/visualizacao-melhorada

echo.
echo 4. Adicionando arquivos ao staging...
git add .

echo.
echo 5. Fazendo commit das alteracoes...
git commit -m "feat nova 

- Adiciona visualizacao em tempo real de criaturas, clusters e guardiao
- Implementa exibicao visual das moedas para todos os elementos
- Adiciona controle de velocidade da simulacao (4 opcoes)
- Melhora interface com estatisticas em tempo real
- Acelera velocidade padrao de 1s para 300ms por iteracao
- Adiciona sombras e melhorias visuais nos elementos
- Implementa painel de configuracoes expandido
- Corrige logica de atualizacao das moedas do guardiao
- Adiciona documentacao completa das funcionalidades"

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

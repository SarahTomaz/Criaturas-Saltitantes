@echo off
setlocal enabledelayedexpansion

echo ==========================================
echo COMMIT DAS MELHORIAS - CRIATURAS SALTITANTES
echo ==========================================
echo.

echo Verificando Git...
where git >nul 2>&1
if !errorlevel! neq 0 (
    echo ERRO: Git nao encontrado no sistema!
    echo Por favor, instale o Git: https://git-scm.com/
    pause
    exit /b 1
)

echo Git encontrado! Iniciando processo...
echo.

echo 1. Inicializando repositorio...
git init

echo.
echo 2. Configurando usuario Git (se necessario)...
git config user.name "Sarah Tomaz" 2>nul
git config user.email "sarahtomaz@email.com" 2>nul

echo.
echo 3. Removendo remote anterior (se existir)...
git remote remove origin 2>nul

echo.
echo 4. Adicionando remote do GitHub...
git remote add origin https://github.com/SarahTomaz/Criaturas-Saltitantes.git

echo.
echo 5. Verificando status dos arquivos...
git status

echo.
echo 6. Adicionando todos os arquivos...
git add .

echo.
echo 7. Criando/mudando para branch de feature...
git checkout -b feature/visualizacao-melhorada 2>nul
if !errorlevel! neq 0 (
    echo Branch ja existe, mudando para ela...
    git checkout feature/visualizacao-melhorada
)

echo.
echo 8. Fazendo commit das alteracoes...
git commit -m "feat nova" 

echo.
echo 9. Fazendo push para o GitHub...
echo IMPORTANTE: Voce pode precisar fazer login no GitHub!
git push -u origin feature/visualizacao-melhorada

if !errorlevel! equ 0 (
    echo.
    echo ==========================================
    echo ✅ COMMIT REALIZADO COM SUCESSO!
    echo ==========================================
    echo.
    echo Branch criada: feature/visualizacao-melhorada
    echo Repository: https://github.com/SarahTomaz/Criaturas-Saltitantes
    echo.
    echo PROXIMOS PASSOS:
    echo 1. Acesse: https://github.com/SarahTomaz/Criaturas-Saltitantes
    echo 2. Crie um Pull Request da branch 'feature/visualizacao-melhorada' para 'main'
    echo 3. Revise as alteracoes e faca o merge
    echo.
    echo Abrindo GitHub no navegador...
    start https://github.com/SarahTomaz/Criaturas-Saltitantes
) else (
    echo.
    echo ==========================================
    echo ❌ ERRO NO PUSH
    echo ==========================================
    echo.
    echo Possiveeis causas:
    echo 1. Problemas de autenticacao com GitHub
    echo 2. Repositorio nao existe ou sem permissao
    echo 3. Problemas de conectividade
    echo.
    echo Solucoes:
    echo 1. Configure suas credenciais: git config --global user.name "Seu Nome"
    echo 2. Configure seu email: git config --global user.email "seu@email.com"
    echo 3. Verifique se o repositorio existe no GitHub
    echo 4. Tente fazer login: gh auth login (se tiver GitHub CLI)
)

echo.
pause

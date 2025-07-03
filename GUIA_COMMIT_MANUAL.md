# 🚀 Guia Manual para Commit no GitHub

## Caso os scripts automáticos não funcionem, siga estes passos:

### 📋 **Pré-requisitos:**
1. Git instalado: https://git-scm.com/
2. Conta GitHub: https://github.com/
3. Repositório criado: https://github.com/SarahTomaz/Criaturas-Saltitantes

---

### 🔧 **Passo 1: Configurar Git (primeira vez)**
```bash
git config --global user.name "Sarah Tomaz"
git config --global user.email "seu@email.com"
```

### 🏠 **Passo 2: Navegar para o diretório**
```bash
cd "c:\Users\Sarah Tomaz\Teste de Software\teste"
```

### 📦 **Passo 3: Inicializar repositório**
```bash
git init
```

### 🔗 **Passo 4: Adicionar remote**
```bash
git remote add origin https://github.com/SarahTomaz/Criaturas-Saltitantes.git
```

### 🌿 **Passo 5: Criar branch**
```bash
git checkout -b feature/visualizacao-melhorada
```

### 📁 **Passo 6: Adicionar arquivos**
```bash
git add .
```

### 💬 **Passo 7: Fazer commit**
```bash
git commit -m "feat: Implementa visualização gráfica completa e controle de velocidade

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
- Documenta condições de parada e velocidades"
```

### 🚀 **Passo 8: Fazer push**
```bash
git push -u origin feature/visualizacao-melhorada
```

---

## 🔐 **Se houver problemas de autenticação:**

### Opção 1: Token de Acesso Pessoal
1. Acesse: https://github.com/settings/tokens
2. Clique em "Generate new token (classic)"
3. Selecione escopo "repo"
4. Copie o token gerado
5. Use o token como senha quando solicitado

### Opção 2: GitHub CLI
```bash
# Instalar: https://cli.github.com/
gh auth login
```

### Opção 3: SSH (mais seguro)
```bash
# Gerar chave SSH
ssh-keygen -t ed25519 -C "seu@email.com"

# Adicionar ao GitHub: https://github.com/settings/keys
# Usar SSH URL
git remote set-url origin git@github.com:SarahTomaz/Criaturas-Saltitantes.git
```

---

## 🎯 **Após o Push bem-sucedido:**

1. **Acesse**: https://github.com/SarahTomaz/Criaturas-Saltitantes
2. **Clique em**: "Compare & pull request" (aparecerá automaticamente)
3. **Preencha o PR**:
   - Título: "Implementa visualização gráfica completa e controle de velocidade"
   - Descrição: Descreva as principais mudanças
4. **Clique em**: "Create pull request"
5. **Revise** as alterações
6. **Clique em**: "Merge pull request"

---

## ✅ **Verificação de Sucesso:**

### No Terminal:
```bash
git status
git log --oneline
git remote -v
```

### No GitHub:
- Branch `feature/visualizacao-melhorada` deve aparecer
- Commits devem estar visíveis
- Pull request pode ser criado

---

## 🆘 **Solução de Problemas Comuns:**

### Git não encontrado:
- Instale: https://git-scm.com/
- Reinicie o terminal após instalação

### Remote já existe:
```bash
git remote remove origin
git remote add origin https://github.com/SarahTomaz/Criaturas-Saltitantes.git
```

### Branch já existe:
```bash
git checkout feature/visualizacao-melhorada
```

### Conflitos:
```bash
git status
git add .
git commit -m "resolve conflicts"
```

---

**💡 Dica**: Se ainda tiver problemas, pode usar o GitHub Desktop (interface gráfica) ou fazer upload manual dos arquivos pelo site do GitHub.

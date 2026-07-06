# Desenvolvimento Backend - Trabalho Final

Repositório contendo a implementação do **Tema 2: Avatares Avançados** utilizando Spring Boot, Spring Profiles, e emulação de AWS S3 com LocalStack.

---

## 👥 Integrantes do Grupo
*   **Thiago Beltrami Dias Batista**

---

## 🎥 Vídeo de Demonstração
*   👉 **[Clique aqui para assistir ao vídeo do projeto](https://youtu.be/kiME6grQFAE)** (Garanta que o link esteja como público ou não listado)

---

## 🚀 Como Executar o Projeto

### Profile 1: Armazenamento Local (`local`)
Salva e serve os avatares diretamente do sistema de arquivos da máquina (pasta `./fs`).
1. Inicie a aplicação `AuthserverApplication` (o profile `local` está ativo por padrão).
2. Execute o script de testes na raiz do projeto:
   ```bash
   ./scripts/test-avatar.sh
   ```

### Profile 2: Armazenamento na Nuvem (`aws`)
Salva e serve os avatares utilizando o AWS S3 emulado pelo LocalStack.
1. Certifique-se de que o Docker está rodando e execute o script para subir o LocalStack:
   ```bash
   ./scripts/run-localstack.sh
   ```
2. Inicie a aplicação no IntelliJ configurando o profile ativo como `aws` (Argumento de VM `-Dspring.profiles.active=aws`).
3. Execute o script de testes:
   ```bash
   ./scripts/test-avatar.sh
   ```
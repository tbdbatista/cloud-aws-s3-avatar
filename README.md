# Desenvolvimento Backend - Trabalho Final

Repositório contendo a implementação do **Tema 2: Avatares Avançados** utilizando Spring Boot, Spring Profiles, e emulação de AWS S3 com LocalStack.

---

## 🛠️ Resumo da Implementação

Este projeto estende o servidor de autenticação Spring Boot (`authserver`) adicionando suporte para definição automática e gerenciamento de fotos de perfil (avatares) dos usuários. A solução conta com os seguintes destaques técnicos:

1. **Abstração de Storage (`StorageService`)**: Interface de salvamento que encapsula e isola o local de gravação. A lógica da aplicação não depende de onde os arquivos são salvos.
2. **Armazenamento Multiprofile**:
   - **Local (`LocalStorageService`)**: Ativado via profile `local`, grava as imagens em diretórios locais (`./fs/avatars/`).
   - **S3 / LocalStack (`S3StorageService`)**: Ativado via profile `aws`, conecta-se à API S3 emulada localmente via Docker. Cria automaticamente o bucket necessário no startup.
3. **Lógica Avançada de Busca e Fallback (`AvatarService`)**:
   - No cadastro do usuário, calcula-se o hash MD5 do e-mail.
   - O servidor tenta fazer o download da imagem de perfil direto do **Gravatar**.
   - Caso o e-mail não possua Gravatar (resposta `404`), o servidor realiza fallback automático fazendo requisição à API **UI-Avatars** para gerar uma imagem PNG contendo as iniciais do nome do usuário.
   - Os bytes da imagem são baixados pelo servidor e enviados para o storage configurado.
4. **Deleção e Regeneração**: Exposição do endpoint `DELETE /users/{id}/avatar` (protegido por JWT) para apagar a foto existente no provedor de storage e iniciar o fluxo de regeneração automática.

---

## 👥 Integrantes do Grupo
*   **Thiago Beltrami Dias Batista**

---

## 🎥 Vídeo de Demonstração
*   👉 **[Clique aqui para assistir ao vídeo do projeto](https://youtu.be/kiME6grQFAE)**

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
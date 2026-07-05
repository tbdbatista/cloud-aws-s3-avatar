#!/usr/bin/env bash
# Script para testar o fluxo completo de criação e regeneração de Avatares

API_URL="http://localhost:8080/api"
UNIQUE_ID=$(date +%s)
EMAIL="fulano_${UNIQUE_ID}@example.com"

echo "Usando o e-mail: $EMAIL"
echo ""

echo "============================================="
echo "1. Criando usuário 'Fulano Sem Gravatar'"
echo "============================================="
USER1_RES=$(curl -s -X POST "$API_URL/users" \
  -H "Content-Type: application/json" \
  -d "{\"email\":\"$EMAIL\",\"password\":\"Password123!\",\"name\":\"Fulano Sem Gravatar\"}")

echo "Resposta de Criação de Usuário:"
echo "$USER1_RES"
echo ""

# Extrai o ID do usuário criado
USER1_ID=$(echo "$USER1_RES" | grep -o '"id":[0-9]*' | head -n 1 | cut -d':' -f2)

if [ -z "$USER1_ID" ]; then
  echo "Erro: Não foi possível criar o usuário ou extrair o ID."
  exit 1
fi

echo "ID do Usuário Criado: $USER1_ID"
echo ""

echo "============================================="
echo "2. Fazendo login com o usuário criado"
echo "============================================="
LOGIN_RES=$(curl -s -X POST "$API_URL/users/login" \
  -H "Content-Type: application/json" \
  -d "{\"email\":\"$EMAIL\",\"password\":\"Password123!\"}")

# Extrai o token JWT
TOKEN=$(echo "$LOGIN_RES" | grep -o '"token":"[^"]*"' | head -n 1 | cut -d'"' -f4)

if [ -z "$TOKEN" ]; then
  echo "Erro: Não foi possível obter o token JWT."
  exit 1
fi

echo "Token JWT obtido com sucesso."
echo ""

echo "============================================="
echo "3. Consultando dados atuais do usuário (mostrando URL do Avatar)"
echo "============================================="
curl -s -X GET "$API_URL/users/$USER1_ID"
echo ""
echo ""

echo "============================================="
echo "4. Deletando/Regenerando o avatar do usuário"
echo "============================================="
curl -i -X DELETE "$API_URL/users/$USER1_ID/avatar" \
  -H "Authorization: Bearer $TOKEN"
echo ""

echo "============================================="
echo "5. Consultando dados do usuário após regeneração"
echo "============================================="
curl -s -X GET "$API_URL/users/$USER1_ID"
echo ""
echo ""

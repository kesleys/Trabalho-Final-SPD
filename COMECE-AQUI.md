# Quarta etapa: agendamentos, baixas e exportação

## 1. Abra a versão atualizada

Extraia o ZIP atualizado em uma pasta nova, por exemplo:
`C:\Users\kesle\OneDrive\Desktop\vacinacao-atualizado`.
Abra no VS Code a pasta interna `vacinacao`, que contém o pom.xml.
Guarde a versão anterior até confirmar que esta funciona.

## 2. Confira banco e usuário

O PostgreSQL deve estar em execução. Se ainda não criou o usuário da aplicação,
execute no Query Tool do pgAdmin conectado ao banco postgres como administrador:

```sql
CREATE ROLE vacinacao_app WITH LOGIN PASSWORD 'SUA_SENHA_LOCAL';
```

Depois execute separadamente:

```sql
CREATE DATABASE vacinacao OWNER vacinacao_app;
```

Se banco e usuário já existem, não repita os comandos. Use suas credenciais existentes.

## 3. Configure a senha somente no seu computador

No PowerShell, dentro da pasta do projeto:

```powershell
Test-Path 'C:\Ferramentas\tomcat\conf\vacinacao.properties'
```

Se retornar False, copie o modelo:

```powershell
Copy-Item '.\config\vacinacao.properties.example' 'C:\Ferramentas\tomcat\conf\vacinacao.properties'
```

Se retornar True, mantenha o arquivo existente e confira seu conteúdo localmente.
Abra o arquivo pelo Bloco de Notas:

```powershell
notepad 'C:\Ferramentas\tomcat\conf\vacinacao.properties'
```

Preencha e salve como UTF-8:

```properties
db.url=jdbc:postgresql://localhost:5432/vacinacao
db.user=vacinacao_app
db.password=SUA_SENHA_LOCAL
```

Use a senha de vacinacao_app, que pode ser diferente da senha do administrador postgres.
Não coloque aspas em volta da senha. Se contiver barra invertida, escreva-a duplicada
(regra do formato .properties). Não compartilhe esse arquivo ou sua senha.
O programa procura a configuração em `conf` de CATALINA_BASE.

## 4. Compile e execute os testes

No terminal da pasta que contém o pom.xml:

```powershell
mvn clean package
```

São 39 testes no total. As dependências são as mesmas da segunda etapa.
Só prossiga se terminar com BUILD SUCCESS e sem falhas nos testes.
Os testes de persistência usam H2 em memória; não gravam no seu banco PostgreSQL.
O arquivo gerado é `target\vacinacao.war`.

## 5. Atualize o Tomcat

Pare a instância que estiver rodando em primeiro plano com Ctrl+C e aguarde encerrar.
No terminal do projeto:

```powershell
Copy-Item '.\target\vacinacao.war' 'C:\Ferramentas\tomcat\webapps\vacinacao.war' -Force
$env:CATALINA_HOME = 'C:\Ferramentas\tomcat'
$env:CATALINA_BASE = $env:CATALINA_HOME
Set-Location $env:CATALINA_HOME
& '.\bin\catalina.bat' run
```

Abra http://localhost:8080/vacinacao/ e use os menus Vacinas, Alergias e Usuários.
O Hibernate cria/atualiza vacinas, alergias, usuarios e usuarios_alergias.
O PostgreSQL deve manter os dados após reiniciar o servidor.

## 6. Valide a etapa no PostgreSQL

1. Cadastre uma vacina fictícia com 3 doses, periodicidade Meses e intervalo 2.
2. No menu Agendamentos, selecione um usuário e essa vacina. Escolha a data de hoje,
   informe um horário e salve. Devem aparecer doses 1, 2 e 3, separadas por dois meses.
3. Dê baixa na primeira dose como Realizada e em outra como Cancelada. Confira
   data_situacao com a data de hoje. As doses agendadas devem manter a data da situação vazia.
4. Confira os filtros de realizadas, canceladas e Agendas de hoje.
5. Crie mais duas agendas de dose única para hoje; faça uma baixa e confira a ordem:
   AGENDADO, REALIZADO, CANCELADO, independentemente do horário.
6. Em Usuários, clique em Agendamentos. Confira data, situação, vacina e dose.
7. Na consulta, selecione um usuário, clique em Filtrar e exporte o CSV. Abra-o e
   confira se contém somente os agendamentos dele, em todas as datas e situações.
8. Reinicie o Tomcat e confira a permanência das agendas e baixas.
9. Exclua somente uma dose fictícia e confira se as demais foram preservadas.

Não recrie o banco. A configuração existente em conf/vacinacao.properties continua
válida. O Hibernate acrescenta a tabela agendas e preserva os dados anteriores.

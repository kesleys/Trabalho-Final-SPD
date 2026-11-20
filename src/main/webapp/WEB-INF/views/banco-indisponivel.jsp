<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!doctype html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Banco indisponível — Vacinação</title>
  <link rel="stylesheet" href="<c:url value='/webjars/bootstrap/5.3.3/css/bootstrap.min.css'/>">
</head>
<body><main class="container py-5">
  <h1>Banco de dados indisponível</h1>
  <p>Confira se o PostgreSQL está em execução e se o arquivo
    <code>conf/vacinacao.properties</code> do Tomcat está preenchido.</p>
  <p>Após corrigir a configuração, reinicie o Tomcat. O detalhe do erro está no terminal do servidor.</p>
  <a class="btn btn-primary" href="<c:url value='/'/>">Voltar ao início</a>
</main></body>
</html>

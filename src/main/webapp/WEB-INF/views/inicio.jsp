<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!doctype html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Agendamento de vacinas</title>
  <link rel="stylesheet" href="<c:url value='/webjars/bootstrap/5.3.3/css/bootstrap.min.css'/>">
  <link rel="stylesheet" href="<c:url value='/assets/app.css'/>">
</head>
<body>
  <%@ include file="nav.jspf" %>
  <main class="container py-5">
    <h1>Agendamento de vacinas</h1>
    <p class="lead">Gerencie vacinas, alergias e as pessoas que receberão as doses.</p>
    <c:choose>
      <c:when test="${bancoDisponivel}">
        <div class="alert alert-success" role="status">Conexão com o banco inicializada.</div>
        <a class="btn btn-primary" href="<c:url value='/vacinas'/>">Gerenciar vacinas</a>
        <a class="btn btn-primary" href="<c:url value='/alergias'/>">Gerenciar alergias</a>
        <a class="btn btn-primary" href="<c:url value='/usuarios'/>">Gerenciar usuários</a>
        <a class="btn btn-primary" href="<c:url value='/agendas'/>">Agendar vacinação</a>
      </c:when>
      <c:otherwise>
        <div class="alert alert-warning" role="alert">
          O banco ainda não está disponível. Configure <code>conf/vacinacao.properties</code>
          no Tomcat, confira o PostgreSQL e reinicie o Tomcat.
        </div>
      </c:otherwise>
    </c:choose>
    <hr class="my-4">
    <h2 class="h5">Próximas etapas</h2>
    <p>Consulte as agendas do dia, registre as baixas e exporte o histórico de um usuário pelo menu Agendamentos.</p>
  </main>
</body>
</html>

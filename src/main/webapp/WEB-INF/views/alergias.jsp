<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!doctype html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Alergias — Vacinação</title>
  <link rel="stylesheet" href="<c:url value='/webjars/bootstrap/5.3.3/css/bootstrap.min.css'/>">
  <link rel="stylesheet" href="<c:url value='/assets/app.css'/>">
  <script defer src="<c:url value='/assets/cadastros.js'/>"></script>
</head>
<body>
  <%@ include file="nav.jspf" %>
  <main class="container py-4">
    <h1>Alergias</h1>
    <p>Cadastre as alergias que poderão ser associadas aos usuários.</p>
    <c:if test="${not empty sucesso}"><div class="alert alert-success" role="status"><c:out value="${sucesso}"/></div></c:if>
    <c:if test="${not empty erro}"><div class="alert alert-danger" role="alert"><c:out value="${erro}"/></div></c:if>
    <section class="card mb-4"><div class="card-body">
      <h2 class="h4">Cadastrar alergia</h2>
      <form method="post" action="<c:url value='/alergias'/>">
        <input type="hidden" name="acao" value="incluir">
        <input type="hidden" name="csrf" value="<c:out value='${sessionScope.csrf}'/>">
        <label class="form-label" for="nome">Nome *</label>
        <input class="form-control mb-3" id="nome" name="nome" required maxlength="40"
               value="<c:out value='${param.acao == "incluir" ? param.nome : ""}'/>">
        <button class="btn btn-primary" type="submit">Cadastrar alergia</button>
      </form>
    </div></section>
    <h2 class="h4">Alergias cadastradas</h2>
    <c:choose><c:when test="${empty alergias}"><p>Nenhuma alergia cadastrada.</p></c:when>
      <c:otherwise><div class="table-responsive"><table class="table table-striped align-middle">
        <thead><tr><th scope="col">ID</th><th scope="col">Nome</th><th scope="col">Ações</th></tr></thead>
        <tbody><c:forEach var="alergia" items="${alergias}"><tr>
          <td>${alergia.id}</td><td><c:out value="${alergia.nome}"/></td>
          <td><form class="excluir-registro" method="post" action="<c:url value='/alergias'/>">
            <input type="hidden" name="acao" value="excluir"><input type="hidden" name="id" value="${alergia.id}">
            <input type="hidden" name="csrf" value="<c:out value='${sessionScope.csrf}'/>">
            <button class="btn btn-outline-danger btn-sm" type="submit">Excluir<span class="visually-hidden"> <c:out value="${alergia.nome}"/></span></button>
          </form></td>
        </tr></c:forEach></tbody>
      </table></div></c:otherwise>
    </c:choose>
  </main>
</body></html>

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!doctype html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Usuários — Vacinação</title>
  <link rel="stylesheet" href="<c:url value='/webjars/bootstrap/5.3.3/css/bootstrap.min.css'/>">
  <link rel="stylesheet" href="<c:url value='/assets/app.css'/>">
  <script defer src="<c:url value='/assets/cadastros.js'/>"></script>
</head>
<body>
  <%@ include file="nav.jspf" %>
  <main class="container py-4">
    <h1>Usuários</h1><p>Cadastre as pessoas que receberão as vacinas e suas alergias.</p>
    <c:if test="${not empty sucesso}"><div class="alert alert-success" role="status"><c:out value="${sucesso}"/></div></c:if>
    <c:if test="${not empty erro}"><div class="alert alert-danger" role="alert"><c:out value="${erro}"/></div></c:if>
    <section class="card mb-4"><div class="card-body">
      <h2 class="h4">Cadastrar usuário</h2>
      <form method="post" action="<c:url value='/usuarios'/>">
        <input type="hidden" name="acao" value="incluir">
        <input type="hidden" name="csrf" value="<c:out value='${sessionScope.csrf}'/>">
        <div class="row g-3">
          <div class="col-md-6"><label class="form-label" for="nome">Nome *</label>
            <input class="form-control" id="nome" name="nome" required maxlength="60" value="<c:out value='${param.acao == "incluir" ? param.nome : ""}'/>"></div>
          <div class="col-md-3"><label class="form-label" for="dataNascimento">Nascimento *</label>
            <input class="form-control" type="date" id="dataNascimento" name="dataNascimento" required max="${hoje}" value="<c:out value='${param.acao == "incluir" ? param.dataNascimento : ""}'/>"></div>
          <div class="col-md-3"><label class="form-label" for="sexo">Sexo *</label>
            <select class="form-select" id="sexo" name="sexo" required>
              <option value="">Selecione</option>
              <option value="M" ${param.acao == 'incluir' && param.sexo == 'M' ? 'selected' : ''}>Masculino</option>
              <option value="F" ${param.acao == 'incluir' && param.sexo == 'F' ? 'selected' : ''}>Feminino</option>
            </select></div>
          <div class="col-md-9"><label class="form-label" for="logradouro">Logradouro</label>
            <input class="form-control" id="logradouro" name="logradouro" maxlength="60" value="<c:out value='${param.acao == "incluir" ? param.logradouro : ""}'/>"></div>
          <div class="col-md-3"><label class="form-label" for="numero">Número</label>
            <input class="form-control" id="numero" name="numero" inputmode="numeric" pattern="[0-9]{1,12}" maxlength="12" value="<c:out value='${param.acao == "incluir" ? param.numero : ""}'/>">
            <div class="form-text">Opcional; somente dígitos.</div></div>
          <div class="col-md-5"><label class="form-label" for="setor">Setor</label>
            <input class="form-control" id="setor" name="setor" maxlength="40" value="<c:out value='${param.acao == "incluir" ? param.setor : ""}'/>"></div>
          <div class="col-md-5"><label class="form-label" for="cidade">Cidade</label>
            <input class="form-control" id="cidade" name="cidade" maxlength="40" value="<c:out value='${param.acao == "incluir" ? param.cidade : ""}'/>"></div>
          <div class="col-md-2"><label class="form-label" for="uf">UF *</label>
            <select class="form-select" id="uf" name="uf" required><option value="">Selecione</option>
              <c:forEach var="uf" items="${ufs}"><option value="${uf}" ${param.acao == 'incluir' && param.uf == uf.name() ? 'selected' : ''}>${uf}</option></c:forEach>
            </select></div>
          <div class="col-12"><fieldset>
            <legend class="h6">Alergias — seleção opcional</legend>
            <c:if test="${empty alergias}"><p class="text-secondary">Nenhuma alergia cadastrada. Você pode cadastrar o usuário sem alergias.</p></c:if>
            <div class="d-flex flex-wrap gap-3">
              <c:forEach var="alergia" items="${alergias}"><div class="form-check">
                <input class="form-check-input" type="checkbox" name="alergias" id="alergia-${alergia.id}" value="${alergia.id}"
                       ${param.acao == 'incluir' && alergiasSelecionadas.contains(alergia.id) ? 'checked' : ''}>
                <label class="form-check-label" for="alergia-${alergia.id}"><c:out value="${alergia.nome}"/></label>
              </div></c:forEach>
            </div>
          </fieldset></div>
          <div class="col-12"><button class="btn btn-primary" type="submit">Cadastrar usuário</button></div>
        </div>
      </form>
    </div></section>
    <h2 class="h4">Usuários cadastrados</h2>
    <c:choose><c:when test="${empty usuarios}"><p>Nenhum usuário cadastrado.</p></c:when>
      <c:otherwise><div class="table-responsive"><table class="table table-striped align-middle">
        <caption>Abra os detalhes para consultar todas as informações do usuário.</caption>
        <thead><tr><th scope="col">ID</th><th scope="col">Nome</th><th scope="col">Nascimento</th><th scope="col">Sexo</th><th scope="col">Detalhes</th><th scope="col">Ações</th></tr></thead>
        <tbody><c:forEach var="usuario" items="${usuarios}"><tr>
          <td>${usuario.id}</td><td><c:out value="${usuario.nome}"/></td>
          <td><c:out value="${usuario.dataNascimentoFormatada}"/></td><td><c:out value="${usuario.sexo.descricao}"/></td>
          <td><details><summary>Endereço e alergias</summary><dl class="mt-2">
            <dt>Logradouro</dt><dd><c:out value="${usuario.logradouro}" default="—"/></dd>
            <dt>Número</dt><dd><c:out value="${usuario.numero}" default="—"/></dd>
            <dt>Setor</dt><dd><c:out value="${usuario.setor}" default="—"/></dd>
            <dt>Cidade / UF</dt><dd><c:out value="${usuario.cidade}" default="—"/> / ${usuario.uf}</dd>
            <dt>Alergias</dt><dd><c:choose><c:when test="${empty usuario.alergias}">Nenhuma alergia informada.</c:when>
              <c:otherwise><ul><c:forEach var="alergia" items="${usuario.alergias}"><li><c:out value="${alergia.nome}"/></li></c:forEach></ul></c:otherwise></c:choose></dd>
          </dl></details></td>
          <td><a class="btn btn-outline-primary btn-sm mb-2" href="<c:url value='/agendas'/>?usuario=${usuario.id}">Agendamentos</a><form class="excluir-registro" method="post" action="<c:url value='/usuarios'/>">
            <input type="hidden" name="acao" value="excluir"><input type="hidden" name="id" value="${usuario.id}">
            <input type="hidden" name="csrf" value="<c:out value='${sessionScope.csrf}'/>">
            <button class="btn btn-outline-danger btn-sm" type="submit">Excluir<span class="visually-hidden"> <c:out value="${usuario.nome}"/></span></button>
          </form></td>
        </tr></c:forEach></tbody>
      </table></div></c:otherwise>
    </c:choose>
  </main>
</body></html>

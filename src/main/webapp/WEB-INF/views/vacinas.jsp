<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!doctype html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Vacinas — Vacinação</title>
  <link rel="stylesheet" href="<c:url value='/webjars/bootstrap/5.3.3/css/bootstrap.min.css'/>">
  <link rel="stylesheet" href="<c:url value='/assets/app.css'/>">
  <script defer src="<c:url value='/assets/vacinas.js'/>"></script>
</head>
<body>
  <%@ include file="nav.jspf" %>
  <main class="container py-4">
    <h1>Vacinas</h1>
    <p>Cadastre as vacinas e os intervalos entre suas doses.</p>
    <c:if test="${not empty sucesso}"><div class="alert alert-success" role="status"><c:out value="${sucesso}"/></div></c:if>
    <c:if test="${not empty erro}"><div class="alert alert-danger" role="alert"><c:out value="${erro}"/></div></c:if>
    <section class="card mb-4" aria-labelledby="titulo-cadastro">
      <div class="card-body">
        <h2 id="titulo-cadastro" class="h4">Cadastrar vacina</h2>
        <form method="post" action="<c:url value='/vacinas'/>">
          <input type="hidden" name="csrf" value="<c:out value='${sessionScope.csrf}'/>">
          <input type="hidden" name="acao" value="incluir">
          <div class="row g-3">
            <div class="col-md-8">
              <label class="form-label" for="titulo">Título *</label>
              <input class="form-control" id="titulo" name="titulo" required maxlength="60"
                     value="<c:out value='${param.acao == "incluir" ? param.titulo : ""}'/>">
            </div>
            <div class="col-md-4">
              <label class="form-label" for="doses">Quantidade de doses *</label>
              <input class="form-control" type="number" id="doses" name="doses" required min="1" step="1"
                     value="<c:out value='${param.acao == "incluir" ? param.doses : "1"}'/>">
            </div>
            <div class="col-12">
              <label class="form-label" for="descricao">Descrição</label>
              <textarea class="form-control" id="descricao" name="descricao" maxlength="200" rows="2"><c:out value='${param.acao == "incluir" ? param.descricao : ""}'/></textarea>
            </div>
            <div class="col-md-6">
              <label class="form-label" for="periodicidade">Periodicidade</label>
              <select class="form-select" id="periodicidade" name="periodicidade" aria-describedby="ajuda-intervalo">
                <option value="">Selecione</option>
                <c:forEach var="periodo" items="${periodicidades}">
                  <option value="${periodo.codigo}" ${param.acao == 'incluir' && periodicidadeSelecionada == periodo.codigo ? 'selected' : ''}><c:out value="${periodo.descricao}"/></option>
                </c:forEach>
              </select>
            </div>
            <div class="col-md-6">
              <label class="form-label" for="intervalo">Intervalo</label>
              <input class="form-control" type="number" id="intervalo" name="intervalo" min="1" step="1"
                     aria-describedby="ajuda-intervalo" value="<c:out value='${param.acao == "incluir" ? param.intervalo : ""}'/>">
            </div>
            <p id="ajuda-intervalo" class="form-text">Periodicidade e intervalo são obrigatórios somente para mais de uma dose.
              Exemplo: 3 doses, periodicidade Meses e intervalo 2.</p>
            <div class="col-12"><button class="btn btn-primary" type="submit">Cadastrar vacina</button></div>
          </div>
        </form>
      </div>
    </section>
    <section aria-labelledby="titulo-lista">
      <h2 id="titulo-lista" class="h4">Vacinas cadastradas</h2>
      <c:choose>
        <c:when test="${consultaFalhou}"><p>Listagem temporariamente indisponível.</p></c:when>
        <c:when test="${empty vacinas}"><p>Nenhuma vacina cadastrada.</p></c:when>
        <c:otherwise>
          <div class="table-responsive">
            <table class="table table-striped align-middle">
              <caption>Informações completas das vacinas cadastradas.</caption>
              <thead><tr><th scope="col">ID</th><th scope="col">Título</th><th scope="col">Descrição</th><th scope="col">Doses</th><th scope="col">Periodicidade</th><th scope="col">Intervalo</th><th scope="col">Ações</th></tr></thead>
              <tbody>
                <c:forEach var="vacina" items="${vacinas}">
                  <tr>
                    <td>${vacina.id}</td><td><c:out value="${vacina.titulo}"/></td>
                    <td class="descricao"><c:out value="${vacina.descricao}" default="—"/></td>
                    <td>${vacina.doses}</td>
                    <td><c:out value="${vacina.periodicidade.descricao}" default="Não se aplica"/></td>
                    <td><c:out value="${vacina.intervalo}" default="—"/></td>
                    <td>
                      <form class="excluir-vacina" method="post" action="<c:url value='/vacinas'/>">
                        <input type="hidden" name="csrf" value="<c:out value='${sessionScope.csrf}'/>">
                        <input type="hidden" name="acao" value="excluir">
                        <input type="hidden" name="id" value="${vacina.id}">
                        <button class="btn btn-outline-danger btn-sm" type="submit">Excluir<span class="visually-hidden"> <c:out value="${vacina.titulo}"/></span></button>
                      </form>
                    </td>
                  </tr>
                </c:forEach>
              </tbody>
            </table>
          </div>
        </c:otherwise>
      </c:choose>
    </section>
  </main>
</body>
</html>

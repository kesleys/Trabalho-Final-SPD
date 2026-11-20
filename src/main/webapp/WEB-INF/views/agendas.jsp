<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!doctype html><html lang="pt-BR"><head>
<meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1">
<title>Agendas — Vacinação</title>
<link rel="stylesheet" href="<c:url value='/webjars/bootstrap/5.3.3/css/bootstrap.min.css'/>">
<link rel="stylesheet" href="<c:url value='/assets/app.css'/>">
<script defer src="<c:url value='/assets/cadastros.js'/>"></script>
</head><body><%@ include file="nav.jspf" %><main class="container py-4">
<h1>Agendamentos</h1>
<c:if test="${not empty erro}"><div class="alert alert-danger" role="alert"><c:out value="${erro}"/></div></c:if>
<c:if test="${not empty sucesso}"><div class="alert alert-success" role="status"><c:out value="${sucesso}"/></div></c:if>
<section class="card mb-4"><div class="card-body"><h2 class="h4">Agendar vacinação</h2>
<p>Ao salvar, serão criadas todas as doses previstas para a vacina, com o mesmo horário.</p>
<c:choose><c:when test="${empty usuarios || empty vacinas}"><p>Cadastre ao menos um usuário e uma vacina para agendar.</p></c:when><c:otherwise>
<form method="post" action="<c:url value='/agendas'/>"><input type="hidden" name="acao" value="incluir"><input type="hidden" name="csrf" value="<c:out value='${sessionScope.csrf}'/>">
<div class="row g-3">
<div class="col-md-6"><label for="usuarioId" class="form-label">Usuário *</label><select class="form-select" id="usuarioId" name="usuarioId" required><option value="">Selecione</option>
<c:forEach var="u" items="${usuarios}"><option value="${u.id}"><c:out value="${u.nome}"/> (#${u.id})</option></c:forEach></select></div>
<div class="col-md-6"><label for="vacinaId" class="form-label">Vacina *</label><select class="form-select" id="vacinaId" name="vacinaId" required><option value="">Selecione</option>
<c:forEach var="v" items="${vacinas}"><option value="${v.id}"><c:out value="${v.titulo}"/> — ${v.doses} dose(s)<c:if test="${v.doses > 1}">, a cada ${v.intervalo} <c:out value="${v.periodicidade.descricao}"/></c:if></option></c:forEach></select></div>
<div class="col-md-6"><label for="dataInicial" class="form-label">Data da primeira dose *</label><input class="form-control" type="date" id="dataInicial" name="dataInicial" required min="0001-01-01" max="9999-12-31" value="<c:out value='${param.dataInicial}'/>"></div>
<div class="col-md-6"><label for="hora" class="form-label">Horário *</label><input class="form-control" type="time" id="hora" name="hora" required value="<c:out value='${param.hora}'/>"></div>
<div class="col-12"><label for="observacoes" class="form-label">Observações</label><textarea class="form-control" id="observacoes" name="observacoes" maxlength="200"><c:out value="${param.observacoes}"/></textarea></div>
<div class="col-12"><button class="btn btn-primary" type="submit">Criar agendamentos das doses</button></div>
</div></form></c:otherwise></c:choose></div></section>
<section class="card mb-4"><div class="card-body"><h2 class="h4">Consulta e histórico por usuário</h2>
<form method="get" action="<c:url value='/agendas'/>"><div class="row g-3">
<div class="col-md-4"><label for="usuario" class="form-label">Usuário</label><select class="form-select" id="usuario" name="usuario"><option value="">Todos</option><c:forEach var="u" items="${usuarios}"><option value="${u.id}" ${param.usuario == u.id.toString() ? 'selected' : ''}><c:out value="${u.nome}"/> (#${u.id})</option></c:forEach></select></div>
<div class="col-md-4"><label for="situacao" class="form-label">Situação</label><select class="form-select" id="situacao" name="situacao"><option value="">Todas</option><c:forEach var="s" items="${situacoes}"><option value="${s}" ${param.situacao == s.name() ? 'selected' : ''}>${s}</option></c:forEach></select></div>
<div class="col-md-4"><label for="dataFiltro" class="form-label">Data</label><input class="form-control" type="date" id="dataFiltro" name="dataFiltro" value="<c:out value='${param.dataFiltro}'/>"></div>
<div class="col-12 d-flex flex-wrap gap-2"><button class="btn btn-primary" type="submit">Filtrar</button><button class="btn btn-outline-primary" name="periodo" value="hoje" type="submit">Agendas de hoje</button><a class="btn btn-outline-secondary" href="<c:url value='/agendas'/>">Limpar filtros</a></div>
</div></form>
<form class="mt-3" method="get" action="<c:url value='/agendas'/>"><input type="hidden" name="formato" value="csv"><input type="hidden" name="usuario" value="<c:out value='${param.usuario}'/>"><button class="btn btn-outline-success" type="submit" ${empty param.usuario ? 'disabled' : ''}>Exportar todos os agendamentos deste usuário em CSV</button><div class="form-text">Selecione um usuário e clique em Filtrar. A exportação inclui todas as datas e situações dele.</div></form>
</div></section>
<h2 class="h4">Agendas cadastradas</h2><p>Em cada dia: agendadas primeiro, depois realizadas e canceladas.</p>
<c:choose><c:when test="${empty agendas}"><p>Nenhum agendamento encontrado.</p></c:when><c:otherwise>
<div class="table-responsive"><table class="table table-striped align-middle"><thead><tr><th>ID</th><th>Data / hora</th><th>Usuário</th><th>Vacina / dose</th><th>Situação</th><th>Data da situação</th><th>Observações</th><th>Ações</th></tr></thead><tbody>
<c:forEach var="a" items="${agendas}"><tr><td>${a.id}</td><td>${a.dataFormatada}<br>${a.hora}</td><td><c:out value="${a.usuario.nome}"/></td><td><c:out value="${a.vacina.titulo}"/><br>Dose ${a.dose}</td><td>${a.situacao}</td><td>${a.dataSituacaoFormatada}</td><td><c:out value="${a.observacoes}" default="—"/></td><td>
<c:if test="${a.situacao == 'AGENDADO'}"><form class="mb-2" method="post" action="<c:url value='/agendas'/>"><input type="hidden" name="csrf" value="<c:out value='${sessionScope.csrf}'/>"><input type="hidden" name="acao" value="baixar"><input type="hidden" name="id" value="${a.id}"><button class="btn btn-success btn-sm mb-1" name="destino" value="REALIZADO" type="submit">Realizada</button><button class="btn btn-warning btn-sm" name="destino" value="CANCELADO" type="submit">Cancelada</button></form></c:if>
<form class="excluir-registro" method="post" action="<c:url value='/agendas'/>"><input type="hidden" name="csrf" value="<c:out value='${sessionScope.csrf}'/>"><input type="hidden" name="acao" value="excluir"><input type="hidden" name="id" value="${a.id}"><button class="btn btn-outline-danger btn-sm" type="submit">Excluir esta dose</button></form>
</td></tr></c:forEach></tbody></table></div></c:otherwise></c:choose>
</main></body></html>

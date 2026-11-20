"use strict";
for (const formulario of document.querySelectorAll(".excluir-registro")) {
  formulario.addEventListener("submit", (evento) => {
    if (!window.confirm("Excluir este registro?")) evento.preventDefault();
  });
}

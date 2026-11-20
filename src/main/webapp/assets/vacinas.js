"use strict";

const doses = document.getElementById("doses");
const periodicidade = document.getElementById("periodicidade");
const intervalo = document.getElementById("intervalo");

function atualizarIntervalo() {
  const variasDoses = Number(doses.value) > 1;
  for (const campo of [periodicidade, intervalo]) {
    campo.disabled = !variasDoses;
    campo.required = variasDoses;
  }
}

doses.addEventListener("input", atualizarIntervalo);
atualizarIntervalo();

for (const formulario of document.querySelectorAll(".excluir-vacina")) {
  formulario.addEventListener("submit", (evento) => {
    if (!window.confirm("Excluir esta vacina?")) evento.preventDefault();
  });
}

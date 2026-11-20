package br.ufg.inf.vacinacao.persistence;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.util.function.Function;

public final class TransacoesJpa {
    private TransacoesJpa() { }

    public static <T> T executar(EntityManagerFactory factory, Function<EntityManager, T> operacao) {
        try (var em = factory.createEntityManager()) {
            var tx = em.getTransaction();
            try {
                tx.begin();
                T resultado = operacao.apply(em);
                tx.commit();
                return resultado;
            } catch (RuntimeException erro) {
                try {
                    if (tx.isActive()) tx.rollback();
                } catch (RuntimeException falha) {
                    erro.addSuppressed(falha);
                }
                throw erro;
            }
        }
    }
}

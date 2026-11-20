package br.ufg.inf.vacinacao.persistence;

import br.ufg.inf.vacinacao.model.Vacina;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import java.util.List;
import java.util.function.Function;

/** Uma EntityManager por operação; a factory compartilhada é segura entre threads. */
public class VacinaRepository {
    private final EntityManagerFactory factory;

    public VacinaRepository(EntityManagerFactory factory) {
        this.factory = factory;
    }

    public List<Vacina> listar() {
        try (EntityManager em = factory.createEntityManager()) {
            return em.createQuery("select v from Vacina v order by lower(v.titulo), v.id", Vacina.class)
                    .getResultList();
        }
    }

    public Vacina incluir(Vacina vacina) {
        return emTransacao(em -> {
            em.persist(vacina);
            return vacina;
        });
    }

    public boolean excluir(int id) {
        return emTransacao(em -> {
            Vacina vacina = em.find(Vacina.class, id);
            if (vacina == null) return false;
            em.remove(vacina);
            return true;
        });
    }

    private <T> T emTransacao(Function<EntityManager, T> operacao) {
        try (EntityManager em = factory.createEntityManager()) {
            EntityTransaction transacao = em.getTransaction();
            try {
                transacao.begin();
                T resultado = operacao.apply(em);
                transacao.commit();
                return resultado;
            } catch (RuntimeException erro) {
                try {
                    if (transacao.isActive()) transacao.rollback();
                } catch (RuntimeException falhaRollback) {
                    erro.addSuppressed(falhaRollback);
                }
                throw erro;
            }
        }
    }
}

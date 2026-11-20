package br.ufg.inf.vacinacao.persistence;

import br.ufg.inf.vacinacao.model.Alergia;
import jakarta.persistence.EntityManagerFactory;
import java.util.List;

public class AlergiaRepository {
    private final EntityManagerFactory factory;
    public AlergiaRepository(EntityManagerFactory factory) { this.factory = factory; }
    public List<Alergia> listar() {
        try (var em = factory.createEntityManager()) {
            return em.createQuery("select a from Alergia a order by lower(a.nome), a.id", Alergia.class)
                    .getResultList();
        }
    }
    public Alergia incluir(String nome) {
        return TransacoesJpa.executar(factory, em -> {
            Alergia alergia = new Alergia(nome);
            em.persist(alergia);
            return alergia;
        });
    }
    public boolean excluir(int id) {
        return TransacoesJpa.executar(factory, em -> {
            Alergia alergia = em.find(Alergia.class, id);
            if (alergia == null) return false;
            long referencias = em.createQuery("select count(u) from Usuario u join u.alergias a where a.id = :id", Long.class)
                    .setParameter("id", id).getSingleResult();
            if (referencias > 0) {
                throw new IllegalArgumentException("Esta alergia está associada a um usuário e não pode ser excluída.");
            }
            em.remove(alergia);
            return true;
        });
    }
}

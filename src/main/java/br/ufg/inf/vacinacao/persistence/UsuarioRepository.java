package br.ufg.inf.vacinacao.persistence;

import br.ufg.inf.vacinacao.model.Alergia;
import br.ufg.inf.vacinacao.model.Usuario;
import jakarta.persistence.EntityManagerFactory;
import java.util.List;
import java.util.Set;

public class UsuarioRepository {
    private final EntityManagerFactory factory;
    public UsuarioRepository(EntityManagerFactory factory) { this.factory = factory; }

    public List<Usuario> listar() {
        try (var em = factory.createEntityManager()) {
            // Carregar as alergias antes de fechar a EntityManager evita lazy loading na JSP.
            return em.createQuery("select distinct u from Usuario u left join fetch u.alergias "
                    + "order by u.nome, u.id", Usuario.class).getResultList();
        }
    }

    public Usuario incluir(Usuario usuario, Set<Integer> idsAlergias) {
        return TransacoesJpa.executar(factory, em -> {
            for (Integer id : idsAlergias) {
                Alergia alergia = em.find(Alergia.class, id);
                if (alergia == null) {
                    throw new IllegalArgumentException("Uma alergia selecionada não existe mais. Atualize a página.");
                }
                usuario.adicionarAlergia(alergia);
            }
            em.persist(usuario);
            return usuario;
        });
    }

    public boolean excluir(int id) {
        return TransacoesJpa.executar(factory, em -> {
            Usuario usuario = em.find(Usuario.class, id);
            if (usuario == null) return false;
            // A associação é removida; as alergias compartilhadas não sofrem cascade REMOVE.
            em.remove(usuario);
            return true;
        });
    }
}

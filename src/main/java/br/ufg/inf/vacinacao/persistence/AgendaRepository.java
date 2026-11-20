package br.ufg.inf.vacinacao.persistence;

import br.ufg.inf.vacinacao.model.*;
import br.ufg.inf.vacinacao.service.CalculadoraDoses;
import jakarta.persistence.*;
import java.time.*;
import java.util.List;

public class AgendaRepository {
    private final EntityManagerFactory factory;
    public AgendaRepository(EntityManagerFactory factory){this.factory=factory;}
    public int agendar(int usuarioId,int vacinaId,LocalDate data,LocalTime hora,String observacoes){
        return TransacoesJpa.executar(factory,em->{
            Usuario usuario=em.find(Usuario.class,usuarioId);
            Vacina vacina=em.find(Vacina.class,vacinaId);
            if(usuario==null || vacina==null) throw new IllegalArgumentException("Selecione usuário e vacina existentes.");
            var doses=new CalculadoraDoses().calcular(data,vacina.getDoses(),vacina.getPeriodicidade(),vacina.getIntervalo());
            if(doses.stream().anyMatch(d->d.data().getYear()>9999)) throw new IllegalArgumentException("O esquema de doses ultrapassa o ano 9999.");
            for(var dose:doses) em.persist(new Agenda(usuario,vacina,dose.data(),hora,dose.numero(),observacoes));
            return doses.size();
        });
    }
    public List<Agenda> listar(Integer usuarioId,LocalDate data,Situacao situacao){
        try(var em=factory.createEntityManager()){
            String hql="select a from Agenda a join fetch a.usuario join fetch a.vacina where 1=1";
            if(usuarioId!=null) hql+=" and a.usuario.id=:usuario";
            if(data!=null) hql+=" and a.data=:data";
            if(situacao!=null) hql+=" and a.situacao=:situacao";
            hql+=" order by a.data, case a.situacao when AGENDADO then 0 when REALIZADO then 1 else 2 end, a.hora, a.id";
            var query=em.createQuery(hql,Agenda.class);
            if(usuarioId!=null) query.setParameter("usuario",usuarioId);
            if(data!=null) query.setParameter("data",data);
            if(situacao!=null) query.setParameter("situacao",situacao);
            return query.getResultList();
        }
    }
    public void baixar(int id,Situacao situacao,LocalDate hoje){
        TransacoesJpa.executar(factory,em->{
            var agenda=em.find(Agenda.class,id,LockModeType.PESSIMISTIC_WRITE);
            if(agenda==null) throw new IllegalArgumentException("Agenda não encontrada.");
            agenda.baixar(situacao,hoje); return null;
        });
    }
    public boolean excluir(int id){
        return TransacoesJpa.executar(factory,em->{
            var agenda=em.find(Agenda.class,id);
            if(agenda==null)return false;
            em.remove(agenda);return true;
        });
    }
}

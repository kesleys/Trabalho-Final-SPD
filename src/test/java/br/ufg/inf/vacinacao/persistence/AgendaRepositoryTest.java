package br.ufg.inf.vacinacao.persistence;

import br.ufg.inf.vacinacao.model.*;
import br.ufg.inf.vacinacao.service.*;
import jakarta.persistence.*;
import java.time.*;
import java.util.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class AgendaRepositoryTest {
    EntityManagerFactory factory; AgendaRepository agendas; Usuario usuario; Vacina vacina;
    @BeforeEach void preparar(){
        factory=Persistence.createEntityManagerFactory("vacinacao",Map.of(
          "jakarta.persistence.jdbc.driver","org.h2.Driver","jakarta.persistence.jdbc.url","jdbc:h2:mem:"+UUID.randomUUID()+";MODE=PostgreSQL",
          "jakarta.persistence.jdbc.user","sa","jakarta.persistence.jdbc.password","","hibernate.hbm2ddl.auto","create","hibernate.hbm2ddl.halt_on_error","true"));
        agendas=new AgendaRepository(factory);
        usuario=new UsuarioRepository(factory).incluir(new Usuario("Pessoa teste",LocalDate.of(2000,1,1),Sexo.M,null,null,null,null,UF.GO),Set.of());
        vacina=new VacinaRepository(factory).incluir(new Vacina("Vacina teste",null,3,Periodicidade.MESES,1));
    }
    @AfterEach void fechar(){factory.close();}
    void agendar(LocalDate dia){agendas.agendar(usuario.getId(),vacina.getId(),dia,LocalTime.of(9,0),"Teste");}
    @Test void criaDosesComCalendarioCorretoESituacaoInicial(){
        agendar(LocalDate.of(2027,1,31));var lista=agendas.listar(null,null,null);
        assertEquals(List.of(LocalDate.of(2027,1,31),LocalDate.of(2027,2,28),LocalDate.of(2027,3,31)),lista.stream().map(Agenda::getData).toList());
        assertEquals(List.of(1,2,3),lista.stream().map(Agenda::getDose).toList());
        assertTrue(lista.stream().allMatch(a->a.getSituacao()==Situacao.AGENDADO&&a.getDataSituacao()==null));
    }
    @Test void falhaNaTerceiraDoseReverteAsDuasPrimeiras(){
        TransacoesJpa.executar(factory,em->{em.createNativeQuery("alter table agendas add constraint teste_dose check(dose <> 3)").executeUpdate();return null;});
        assertThrows(PersistenceException.class,()->agendar(LocalDate.of(2026,11,19)));
        assertTrue(agendas.listar(null,null,null).isEmpty());
    }
    @Test void baixaGuardaDataEPreservaAoRepetir(){
        agendar(LocalDate.of(2026,11,19));int id=agendas.listar(null,null,null).get(0).getId();
        agendas.baixar(id,Situacao.REALIZADO,LocalDate.of(2026,11,20));
        agendas.baixar(id,Situacao.REALIZADO,LocalDate.of(2026,11,21));
        var a=agendas.listar(null,null,Situacao.REALIZADO).get(0);
        assertEquals(LocalDate.of(2026,11,20),a.getDataSituacao());
        assertThrows(IllegalArgumentException.class,()->agendas.baixar(id,Situacao.CANCELADO,LocalDate.of(2026,11,22)));
        assertThrows(IllegalArgumentException.class,()->agendas.baixar(id,Situacao.AGENDADO,LocalDate.of(2026,11,22)));
    }
    @Test void filtraDataUsuarioESituacaoEOrdenaHoje(){
        var unica=new VacinaRepository(factory).incluir(new Vacina("Única",null,1,null,null));
        LocalDate hoje=LocalDate.of(2026,11,19);
        for(int i=0;i<3;i++)agendas.agendar(usuario.getId(),unica.getId(),hoje,LocalTime.of(8+i,0),null);
        var lista=agendas.listar(null,hoje,null);
        agendas.baixar(lista.get(0).getId(),Situacao.CANCELADO,hoje);
        agendas.baixar(lista.get(1).getId(),Situacao.REALIZADO,hoje);
        assertEquals(List.of(Situacao.AGENDADO,Situacao.REALIZADO,Situacao.CANCELADO),agendas.listar(usuario.getId(),hoje,null).stream().map(Agenda::getSituacao).toList());
        assertEquals(1,agendas.listar(usuario.getId(),null,Situacao.CANCELADO).size());
        assertTrue(agendas.listar(9999,null,null).isEmpty());
    }
    @Test void excluiSomenteUmaDoseEProtegeEntidadesReferenciadas(){
        agendar(LocalDate.of(2026,11,19));int id=agendas.listar(null,null,null).get(0).getId();
        assertThrows(PersistenceException.class,()->new VacinaRepository(factory).excluir(vacina.getId()));
        assertThrows(PersistenceException.class,()->new UsuarioRepository(factory).excluir(usuario.getId()));
        assertTrue(agendas.excluir(id));assertEquals(2,agendas.listar(null,null,null).size());
    }
    @Test void rejeitaReferenciasInexistentesEDadosInvalidos(){
        var service=new AgendaService(agendas);
        assertThrows(IllegalArgumentException.class,()->service.agendar("99999",vacina.getId().toString(),"2026-11-19","09:00",null));
        assertThrows(IllegalArgumentException.class,()->service.agendar(usuario.getId().toString(),vacina.getId().toString(),"2026-02-30","09:00",null));
        assertThrows(IllegalArgumentException.class,()->service.agendar(usuario.getId().toString(),vacina.getId().toString(),"2026-11-19","25:00",null));
        assertTrue(agendas.listar(null,null,null).isEmpty());
    }
}

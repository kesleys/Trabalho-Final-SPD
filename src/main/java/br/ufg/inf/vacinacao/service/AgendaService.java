package br.ufg.inf.vacinacao.service;

import br.ufg.inf.vacinacao.model.*;
import br.ufg.inf.vacinacao.persistence.AgendaRepository;
import java.time.*;
import java.util.List;

public class AgendaService {
    private final AgendaRepository repository;
    private final Clock clock;
    public AgendaService(AgendaRepository repository){this(repository,Clock.system(ZoneId.of("America/Sao_Paulo")));}
    public AgendaService(AgendaRepository repository,Clock clock){this.repository=repository;this.clock=clock;}
    public LocalDate hoje(){return LocalDate.now(clock);}
    public int agendar(String usuario,String vacina,String data,String hora,String observacoes){
        try {
            LocalDate dia=data(data);
            if(dia==null)throw new IllegalArgumentException("Informe a data inicial.");
            LocalTime horario=LocalTime.parse(hora==null?"":hora);
            return repository.agendar(Validacao.id(usuario),Validacao.id(vacina),dia,horario,
                    Validacao.texto(observacoes,"Observações",200,false));
        }catch(DateTimeException|ArithmeticException erro){throw new IllegalArgumentException("Confira a data, o horário e o intervalo das doses.");}
    }
    public List<Agenda> listar(String usuario,String dia,String situacao){
        return repository.listar(usuario==null||usuario.isBlank()?null:Validacao.id(usuario),data(dia),situacao(situacao));
    }
    public void baixar(String id,String destino){repository.baixar(Validacao.id(id),situacao(destino),hoje());}
    public boolean excluir(String id){return repository.excluir(Validacao.id(id));}
    public static LocalDate data(String valor){
        if(valor==null||valor.isBlank())return null;
        try{
            LocalDate data=LocalDate.parse(valor);
            if(data.getYear()<1 || data.getYear()>9999)throw new IllegalArgumentException("Use uma data entre os anos 1 e 9999.");
            return data;
        }catch(DateTimeException erro){throw new IllegalArgumentException("Data inválida.");}
    }
    public static Situacao situacao(String valor){
        if(valor==null||valor.isBlank())return null;
        try{return Situacao.valueOf(valor);}catch(IllegalArgumentException erro){throw new IllegalArgumentException("Situação inválida.");}
    }
}

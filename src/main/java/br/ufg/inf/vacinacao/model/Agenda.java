package br.ufg.inf.vacinacao.model;

import jakarta.persistence.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import org.hibernate.annotations.Check;

@Entity @Table(name="agendas")
@Check(constraints="dose > 0 and ((situacao = 'AGENDADO' and data_situacao is null) or (situacao in ('REALIZADO','CANCELADO') and data_situacao is not null))")
public class Agenda {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Integer id;
    @ManyToOne(optional=false) @JoinColumn(name="usuario_id", nullable=false) private Usuario usuario;
    @ManyToOne(optional=false) @JoinColumn(name="vacina_id", nullable=false) private Vacina vacina;
    @Column(nullable=false) private LocalDate data;
    @Column(nullable=false) private LocalTime hora;
    @Column(nullable=false) private int dose;
    @Enumerated(EnumType.STRING) @Column(nullable=false,length=10) private Situacao situacao=Situacao.AGENDADO;
    @Column(name="data_situacao") private LocalDate dataSituacao;
    @Column(length=200) private String observacoes;
    protected Agenda() { }
    public Agenda(Usuario usuario, Vacina vacina, LocalDate data, LocalTime hora, int dose, String observacoes) {
        this.usuario=usuario; this.vacina=vacina; this.data=data; this.hora=hora; this.dose=dose; this.observacoes=observacoes;
    }
    public void baixar(Situacao destino, LocalDate hoje) {
        if(destino==null || destino==Situacao.AGENDADO) throw new IllegalArgumentException("Selecione Realizado ou Cancelado.");
        if(situacao==destino) return;
        if(situacao!=Situacao.AGENDADO) throw new IllegalArgumentException("Esta agenda já recebeu baixa.");
        situacao=destino; dataSituacao=hoje;
    }
    public Integer getId(){return id;}
    public Usuario getUsuario(){return usuario;}
    public Vacina getVacina(){return vacina;}
    public LocalDate getData(){return data;}
    public String getDataFormatada(){return data.format(DateTimeFormatter.ofPattern("dd/MM/uuuu"));}
    public LocalTime getHora(){return hora;}
    public int getDose(){return dose;}
    public Situacao getSituacao(){return situacao;}
    public LocalDate getDataSituacao(){return dataSituacao;}
    public String getDataSituacaoFormatada(){return dataSituacao==null?"—":dataSituacao.format(DateTimeFormatter.ofPattern("dd/MM/uuuu"));}
    public String getObservacoes(){return observacoes;}
}

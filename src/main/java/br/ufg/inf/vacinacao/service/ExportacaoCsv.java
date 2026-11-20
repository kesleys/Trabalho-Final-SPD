package br.ufg.inf.vacinacao.service;

import br.ufg.inf.vacinacao.model.Agenda;
import java.util.List;

public final class ExportacaoCsv {
    private ExportacaoCsv(){}
    public static String gerar(List<Agenda> agendas){
        StringBuilder csv=new StringBuilder("\uFEFFUsuário;Data;Hora;Situação;Vacina;Dose;Data da situação;Observações\r\n");
        for(Agenda a:agendas){
            String[] campos={a.getUsuario().getNome(),a.getDataFormatada(),a.getHora().toString(),
                a.getSituacao().name(),a.getVacina().getTitulo(),Integer.toString(a.getDose()),
                a.getDataSituacaoFormatada(),a.getObservacoes()};
            for(int i=0;i<campos.length;i++){if(i>0)csv.append(';');csv.append(celula(campos[i]));}
            csv.append("\r\n");
        }
        return csv.toString();
    }
    public static String celula(String valor){
        String texto=valor==null?"":valor;
        String inicio=texto.stripLeading();
        if(!inicio.isEmpty() && "=+-@".indexOf(inicio.charAt(0))>=0 || texto.startsWith("\t") || texto.startsWith("\r") || texto.startsWith("\n"))texto="'"+texto;
        return "\""+texto.replace("\"","\"\"")+"\"";
    }
}

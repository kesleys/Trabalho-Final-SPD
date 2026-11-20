package br.ufg.inf.vacinacao.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class PeriodicidadeConverter implements AttributeConverter<Periodicidade, Integer> {
    @Override
    public Integer convertToDatabaseColumn(Periodicidade valor) {
        return valor == null ? null : valor.getCodigo();
    }

    @Override
    public Periodicidade convertToEntityAttribute(Integer codigo) {
        return codigo == null ? null : Periodicidade.porCodigo(codigo);
    }
}

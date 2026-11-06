package model;

import java.math.BigDecimal;

public class Questao {

    private int id;
    private String enunciado;
    private String alternativas;
    private String gabarito;
    private int anoEdicao;
    private int nivelDificuldade;
    private BigDecimal frequenciaHistorica;
    private int temaId;

    public Questao() {
    }

    public Questao(int id, String enunciado, String alternativas,
                   String gabarito, int anoEdicao, int nivelDificuldade,
                   BigDecimal frequenciaHistorica, int temaId) {
        this.id = id;
        this.enunciado = enunciado;
        this.alternativas = alternativas;
        this.gabarito = gabarito;
        this.anoEdicao = anoEdicao;
        this.nivelDificuldade = nivelDificuldade;
        this.frequenciaHistorica = frequenciaHistorica;
        this.temaId = temaId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEnunciado() {
        return enunciado;
    }

    public void setEnunciado(String enunciado) {
        this.enunciado = enunciado;
    }

    public String getAlternativas() {
        return alternativas;
    }

    public void setAlternativas(String alternativas) {
        this.alternativas = alternativas;
    }

    public String getGabarito() {
        return gabarito;
    }

    public void setGabarito(String gabarito) {
        this.gabarito = gabarito;
    }

    public int getAnoEdicao() {
        return anoEdicao;
    }

    public void setAnoEdicao(int anoEdicao) {
        this.anoEdicao = anoEdicao;
    }

    public int getNivelDificuldade() {
        return nivelDificuldade;
    }

    public void setNivelDificuldade(int nivelDificuldade) {
        this.nivelDificuldade = nivelDificuldade;
    }

    public BigDecimal getFrequenciaHistorica() {
        return frequenciaHistorica;
    }

    public void setFrequenciaHistorica(BigDecimal frequenciaHistorica) {
        this.frequenciaHistorica = frequenciaHistorica;
    }

    public int getTemaId() {
        return temaId;
    }

    public void setTemaId(int temaId) {
        this.temaId = temaId;
    }
}
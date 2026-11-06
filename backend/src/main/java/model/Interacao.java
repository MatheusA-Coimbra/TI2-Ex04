package model;

import java.time.LocalDateTime;

public class Interacao {

    private int id;
    private int estudanteId;
    private int questaoId;
    private String alternativaEscolhida;
    private boolean correta;
    private LocalDateTime dataHora;


public Interacao() {
}

public Interacao(int id, int estudanteId, int questaoId, 
                 String alternativaEscolhida, boolean correta, 
                 LocalDateTime dataHora) {

                this.id = id;
                this.estudanteId = estudanteId;
                this.questaoId = questaoId;
                this.alternativaEscolhida = alternativaEscolhida;
                this.correta = correta;
                this.dataHora = dataHora;
                 }

public int getId() {
    return id;
}

public void setId(int id) {
    this.id = id;
}

public int getEstudanteId() {
    return estudanteId;
}

public void setEstudanteId(int estudanteId) {
    this.estudanteId = estudanteId;
}

public int getQuestaoId() {
    return questaoId;
}

public void setQuestaoId(int questaoId) {
    this.questaoId = questaoId;
}

public String getAlternativaEscolhida() {
    return alternativaEscolhida;
}

public void setAlternativaEscolhida(String alternativaEscolhida) {
    this.alternativaEscolhida = alternativaEscolhida;
}

public boolean isCorreta() {
    return correta;
}

public void setCorreta(boolean correta) {
    this.correta = correta;
}

public LocalDateTime getDataHora() {
    return dataHora;
}

public void setDataHora(LocalDateTime dataHora) {
    this.dataHora = dataHora;
}

}